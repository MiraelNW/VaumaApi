package com.miraelDev.demo;


import com.miraelDev.demo.models.dbModels.*;
import com.miraelDev.demo.models.responseDto.AnimeResponseDto;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.payload.FavouriteAnimeRequest;
import com.miraelDev.demo.servises.*;
import com.miraelDev.demo.shikimory.ApiFactory;
import com.miraelDev.demo.shikimory.Episode;
import com.miraelDev.demo.shikimory.JutSu;
import com.miraelDev.demo.shikimory.dto.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.jcodec.api.FrameGrab;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.AWTUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("api/v1/anime")
public class CommonController {

    @Autowired
    private PagingService pagingService;
    @Autowired
    private AnimeService animeService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private GenreService genreService;
    @Autowired
    private SimilarAnimeService similarAnimeService;
    @Autowired
    private SearchService searchService;

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private HttpClient client = HttpClient.newBuilder()
            .build();

    private Integer findIdByAnimeNameInGraphQl(String animeName) {
        WebClient wc = WebClient.create("https://shikimori.one/api/graphql");
        GraphQlClient client = HttpGraphQlClient.create(wc);
        String document = """
                    query ($search: String) {
                      animes(search: $search) {
                                id
                      }
                    }
                """;

        AnimeDto response = client.document(document)
                .variable("search", animeName)
                .execute()
                .block()
                .toEntity(AnimeDto.class);

        return response.getAnimes()[0].getId();
    }

    private Set<VideoDbModel> loadVideos(String animeLink, Integer animeId, AnimeDbModel dbModel) {
        try {

            JutSu inst = new JutSu(animeLink);

            List<Episode> episodes = inst.getAllEpisodes();

            Set<VideoDbModel> videoDbModelList = new HashSet<>();

            List<Thread> qualityThreads = new LinkedList<>();
            List<Thread> seriesThreads = new LinkedList<>();

            for (Episode episode : episodes) {
                System.out.println(episode.name);

                String series = episode.href
                        .substring(
                                episode.href.lastIndexOf("/") + 1,
                                episode.href.lastIndexOf(".")
                        );

                VideoDbModel videoDbModel = VideoDbModel.builder()
                        .videoName(episode.name)
                        .build();

                String seriesDirPath = "C:\\Users\\1\\Desktop\\videos" + "\\" + animeId + "\\" + episode.season + "\\" + series + "\\";

                if (!new File(seriesDirPath).exists())
                    new File(seriesDirPath).mkdirs();

                String link480 = null;

                link480 = inst.getDownloadLink(episode.href, "480p");

                if (link480 != null) {

                    downloadVideo(
                            inst.getClient(),
                            link480,
                            seriesDirPath + "480" + "\\",
                            seriesDirPath + "480" + "\\" + episode.name + ".mp4"
                    );

                    String link = seriesDirPath + "480" + "\\" + episode.name + ".mp4";
                    Path path = Paths.get(seriesDirPath);

                    File file = new File(link);

                    int frameNumber = 360;

                    try {
                        Picture picture = FrameGrab.getFrameFromFile(file, frameNumber);
                        BufferedImage bufferedImage = AWTUtil.toBufferedImage(picture);
                        ImageIO.write(bufferedImage, "png", new File(path + "\\thumbnail.png"));
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    videoDbModel.setVideoUrl480("/api/v1/videos/" + animeId + "/" + episode.season + "/" + series + "/480/" + episode.name + ".mp4");
                    videoDbModel.setVideoImage("/api/v1/videos/" + animeId + "/" + episode.season + "/" + series + "/480/" + episode.name + ".png");

                }
//
                String link720 = null;

                link720 = inst.getDownloadLink(episode.href, "720p");

                if (link720 != null) {

                    downloadVideo(
                            inst.getClient(),
                            link720,
                            seriesDirPath + "720" + "\\",
                            seriesDirPath + "720" + "\\" + episode.name + ".mp4"
                    );

                    videoDbModel.setVideoUrl720("/api/v1/videos/" + animeId + "/" + episode.season + "/" + series + "/720/" + episode.name + ".mp4");

                }


                String link1080 = null;
                link1080 = inst.getDownloadLink(episode.href, "1080p");


                if (link1080 != null) {

                    downloadVideo(
                            inst.getClient(),
                            link1080,
                            seriesDirPath + "1080" + "\\",
                            seriesDirPath + "1080" + "\\" + episode.name + ".mp4"
                    );

                    videoDbModel.setVideoUrl1080("/api/v1/videos/" + animeId + "/" + episode.season + "/" + series + "/1080/" + episode.name + ".mp4");


                }


                videoDbModel.setAnime(dbModel);

                videoDbModelList.add(videoDbModel);
            }

            return videoDbModelList;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private Map<String, String> getAnimeNamesFromJutsu() throws IOException, InterruptedException {

        Map<String, String> animeLinks = new HashMap();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jut.su/anime/2008-2014/"))
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Document doc = Jsoup.parse(response.body());

        Elements episodes = doc.select("div.all_anime_global");

        for (Element el : episodes) {
            if (el.select("div.aaname").text().equals("Ван Пис")) continue;

            animeLinks.put("https://jut.su" + el.select("a").attr("href"), el.select("div.aaname").text());
        }
        return animeLinks;
    }

    private AnimeSeasonModel getAnimeSeasonsFromJutsu(String link, String name) throws
            IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Document doc = Jsoup.parse(response.body());

        Elements titles = doc.select("h2.b-b-title");
        Elements seasonLinks = doc.select("div.the_invis");
        Elements videoLinks = doc.select("a.short-btn");

        List<String> titlesList = new LinkedList<>();
        List<String> seasonsLinksList = new LinkedList<>();
        List<String> filmLinksList = new LinkedList<>();
        List<String> filmNamesList = new LinkedList<>();

        for (Element el : seasonLinks) {
            seasonsLinksList.add("https://jut.su/" + el.select("a").attr("href"));
        }
        for (Element el : titles) {
            titlesList.add(el.text());
        }
        for (Element el : videoLinks) {
            if (el.attr("href").contains("film")) {
                filmLinksList.add("https://jut.su/" + el.attr("href"));
            }
        }

        for (String title : titlesList) {
            if (!title.contains("сезон")) {
                titlesList.remove(title);
            }
        }
        AtomicInteger count = new AtomicInteger();
        if (!filmLinksList.isEmpty()) {
            filmLinksList.forEach((filmLink) -> {
                count.getAndIncrement();
                HttpRequest filmRequest = HttpRequest.newBuilder()
                        .uri(URI.create(filmLink))
                        .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                        .build();

                try {
                    HttpResponse<String> filmResponse = client.send(filmRequest, HttpResponse.BodyHandlers.ofString());

                    Document filmDoc = Jsoup.parse(filmResponse.body());

                    String filmName = filmDoc.select("div.video_plate_title").select("h2").text();

                    if (filmName.isEmpty()) {
                        filmNamesList.add(count.get() + " фильм");
                    } else {
                        filmNamesList.add(filmName);
                    }


                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        AnimeSeasonModel animeSeasonModel = new AnimeSeasonModel();

        if (seasonsLinksList.isEmpty()) {
            animeSeasonModel.setSeasonLinks(List.of(link));
            animeSeasonModel.setNamesForGraphQl(List.of(name));
            animeSeasonModel.setFilmsLinks(filmLinksList);
            animeSeasonModel.setFilmsNameForGraphQl(filmNamesList);
        } else {
            animeSeasonModel.setSeasonLinks(seasonsLinksList);
            animeSeasonModel.setNamesForGraphQl(titlesList);
            animeSeasonModel.setFilmsLinks(filmLinksList);
            animeSeasonModel.setFilmsNameForGraphQl(filmNamesList);
        }
        return animeSeasonModel;
    }

    private void loadAnime(String animeName, String seasonUrl) throws IOException, ParseException {
        Integer animeId = findIdByAnimeNameInGraphQl(animeName);
        System.out.println("anime id: " + animeId);
        AnimeInfoDto dto = ApiFactory.apiService.getAnime(animeId).execute().body();
        List<SimilarAnimeDto> similarDto = ApiFactory.apiService.getSimilarAnime(animeId).execute().body();

        if (dto != null) {

            Date date;

            if (dto.getReleased_on() == null) {
                date = formatter.parse(dto.getAired_on());
            } else {
                date = formatter.parse(dto.getReleased_on());
            }

            AnimeDbModel dbModel = AnimeDbModel.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .russian(dto.getRussian())
                    .kind(dto.getKind())
                    .score(dto.getScore())
                    .status(dto.getStatus())
                    .episodes(dto.getEpisodes())
                    .episodes_aired(dto.getEpisodes_aired())
                    .description(dto.getDescription())
                    .description_rus(dto.getDescription_rus())
                    .airedOn(dto.getAired_on())
                    .releasedOn(date)
                    .rating(dto.getRating())
                    .duration(dto.getDuration())
                    .favoured(dto.getFavoured())
                    .build();

            Set<GenreDbModel> genreDbModelSet = new HashSet<>();

            for (GenreDto genreDto : dto.getGenres()) {
                GenreDbModel genreDbModel = GenreDbModel.builder()
                        .id(genreDto.getId())
                        .name(genreDto.getName())
                        .russian(genreDto.getRussian())
                        .build();
                genreDbModelSet.add(genreDbModel);
            }

            dbModel.setGenres(genreDbModelSet);

            Set<SimilarAnimeDbModel> similarAnimeDbModels = new HashSet<>(SimilarAnimeDto.toDbModelList(similarDto));

            dbModel.setSimilar(similarAnimeDbModels);

            downloadFiles("https://shikimori.one/" + dto.getImage().getPreview(), "C:\\Users\\1\\Desktop\\animes\\preview\\" + dbModel.getId() + ".png", 1);
            downloadFiles("https://shikimori.one/" + dto.getImage().getOriginal(), "C:\\Users\\1\\Desktop\\animes\\original\\" + dbModel.getId() + ".png", 1);

            dbModel.setImage(
                    ImageDbModel.builder()
                            .original("/api/v1/anime/images/original/" + dbModel.getId())
                            .preview("/api/v1/anime/images/preview/" + dbModel.getId())
                            .build()
            );

            System.out.println("db model:" + dbModel);

            Set<VideoDbModel> videoDbModelList = loadVideos(seasonUrl, animeId, dbModel);

            dbModel.setVideoDbModels(videoDbModelList);

            System.out.println("video db model list: " + videoDbModelList);

            videoService.saveAll(videoDbModelList);
            genreService.saveAll(genreDbModelSet);
            similarAnimeService.saveAll(similarAnimeDbModels);
            animeService.save(dbModel);
        }
    }

    private Integer getSeriesCount(String link) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .build();


        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Document doc = Jsoup.parse(response.body());

        Elements series = doc.select("a.short-btn");

        return series.size();
    }

    @GetMapping
    public void main() throws IOException, URISyntaxException, InterruptedException {

        Map<String, String> animeNames = getAnimeNamesFromJutsu();

        animeNames.forEach((link, name) -> {

            try {

                Integer count = getSeriesCount(link);

                if (count < 16) {
                    AnimeSeasonModel animeSeasonModel = getAnimeSeasonsFromJutsu(link, name);

                    System.out.println(animeSeasonModel);

                    for (int i = 0; i < animeSeasonModel.getSeasonLinks().size(); i++) {

                        String animeName = animeSeasonModel.getNamesForGraphQl().get(i);
                        String seasonUrl = animeSeasonModel.getSeasonLinks().get(i);
                        System.out.println("animename: " + animeName);
                        System.out.println("seasonurl: " + seasonUrl);
                        loadAnime(animeName, seasonUrl);

                    }

                    for (int i = 0; i < animeSeasonModel.getFilmsLinks().size(); i++) {

                        String animeFilmName = animeSeasonModel.getFilmsNameForGraphQl().get(i);
                        String filmUrl = animeSeasonModel.getFilmsLinks().get(i);

                        System.out.println("animeFilmName: " + animeFilmName);
                        System.out.println("filmUrl: " + filmUrl);

                        loadAnime(animeFilmName, filmUrl);

                    }
                    System.out.println("\n\n\n");
                }


            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Async
    public static void downloadVideo(HttpClient client, String link, String dirPath, String path) throws
            IOException, InterruptedException {


        if (!new File(dirPath).exists())
            new File(dirPath).mkdirs();


        System.out.println("Start downloading " + path);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(link))
                .setHeader("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2")
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream is = response.body(); OutputStream fos = new FileOutputStream(path)) {
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) != -1)
                fos.write(buffer, 0, length);
        }


        System.out.println(path + " downloaded!");
    }


    private static void downloadFiles(String strURL, String strPath, int buffSize) {
        try {
            URL connection = new URL(strURL);
            HttpURLConnection urlconn;
            urlconn = (HttpURLConnection) connection.openConnection();
            urlconn.setRequestMethod("GET");
            urlconn.connect();
            InputStream in = null;
            in = urlconn.getInputStream();
            OutputStream writer = new FileOutputStream(strPath);
            byte[] buffer = new byte[buffSize];
            int c = in.read(buffer);
            while (c > 0) {
                writer.write(buffer, 0, c);
                c = in.read(buffer);
            }
            writer.flush();
            writer.close();
            in.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @GetMapping("/new")
    public ResponseEntity<PagingResponseDto> getNewAnime(@RequestParam("page") Integer
                                                                 page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getNewAnime(page, pageSize);
    }

    @GetMapping("/popular")
    public ResponseEntity<PagingResponseDto> getPopularAnime(@RequestParam("page") Integer
                                                                     page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getPopularAnime(page, pageSize);
    }

    @GetMapping("/name")
    public ResponseEntity<PagingResponseDto> getNameAnime(@RequestParam("page") Integer
                                                                  page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getNameAnime(page, pageSize);
    }

    @GetMapping("/film")
    public ResponseEntity<PagingResponseDto> getFilmAnime(@RequestParam("page") Integer
                                                                  page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getFilmAnime(page, pageSize);
    }

    @GetMapping("/id")
    public ResponseEntity<AnimeResponseDto> getAnimeById(@RequestParam("anime_id") Long
                                                                 animeId, @RequestParam("user_id") Long userId) {
        return animeService.getAnimeById(animeId, userId);
    }

    @PostMapping("/id/favourite")
    public ResponseEntity<String> setAnimeFavouriteStatus(@RequestBody FavouriteAnimeRequest
                                                                  favouriteAnimeRequest) {
        System.out.println(favouriteAnimeRequest);
        return animeService.setAnimeFavouriteStatus(
                favouriteAnimeRequest.getAnime_id(),
                favouriteAnimeRequest.getUser_id(),
                favouriteAnimeRequest.getIs_favourite()
        );
    }

    @GetMapping("/search")
    public ResponseEntity<PagingResponseDto> searchAnime(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "genres", required = false) String genreCode,
            @RequestParam(value = "date", required = false) String dateCode,
            @RequestParam(value = "sort", required = false) String sortCode,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "page_size") Integer pageSize
    ) {
        return searchService.searchAnime(
                name,
                genreCode,
                dateCode,
                sortCode,
                page,
                pageSize
        );
    }

}
