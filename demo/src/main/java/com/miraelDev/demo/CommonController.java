package com.miraelDev.demo;


import com.miraelDev.demo.models.dbModels.*;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.payload.FavouriteAnimeRequest;
import com.miraelDev.demo.servises.*;
import com.miraelDev.demo.shikimory.ApiFactory;
import com.miraelDev.demo.shikimory.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


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

    private void loadAnime(Integer id) throws IOException, ParseException {
        System.out.println("anime id: " + id);
        AnimeInfoDto dto = ApiFactory.apiService.getAnime(id).execute().body();
        List<SimilarAnimeDto> similarDto = ApiFactory.apiService.getSimilarAnime(id).execute().body();

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

            Set<VideoDbModel> videoDbModelList = getLinksFromDownloadedVideos(id);

            dbModel.setVideoDbModels(videoDbModelList);

            System.out.println("video db model list: " + videoDbModelList);

            videoService.saveAll(videoDbModelList);
            genreService.saveAll(genreDbModelSet);
            similarAnimeService.saveAll(similarAnimeDbModels);
            animeService.save(dbModel);
        }
    }

    private Set<VideoDbModel> getLinksFromDownloadedVideos(Integer id) {
        Set<VideoDbModel> result = new LinkedHashSet<>();
        File[] animeIds = Paths.get("D:/videos/" + id.toString()).toFile().listFiles();
        Arrays.stream(Objects.requireNonNull(animeIds)).toList().forEach(animeId -> {
                    String animeIdName = animeId.getName();
                    Arrays.stream(Objects.requireNonNull(animeId.listFiles())).toList().forEach(season -> {
                                String seasonName = season.getName();
                                Arrays.stream(Objects.requireNonNull(season.listFiles())).toList().forEach(serie -> {
                                            VideoDbModel videoDbModel = new VideoDbModel();
                                            String serieName = serie.getName();
                                            File[] qualitiesAndImageFiles = serie.listFiles();
                                            String quality480 = qualitiesAndImageFiles[0].listFiles()[0].getName();
                                            String quality720 = qualitiesAndImageFiles[1].listFiles()[0].getName();
                                            String quality1080 = qualitiesAndImageFiles[2].listFiles()[0].getName();
                                            String imageName = qualitiesAndImageFiles[3].getName();
                                            videoDbModel.setVideoUrl480(animeIdName + "/" + seasonName + "/" + serieName + "/480/" + quality480);
                                            videoDbModel.setVideoUrl720(animeIdName + "/" + seasonName + "/" + serieName + "/720/" + quality720);
                                            videoDbModel.setVideoUrl1080(animeIdName + "/" + seasonName + "/" + serieName + "/1080/" + quality1080);
                                            videoDbModel.setVideoImage(imageName);
                                            videoDbModel.setVideoName(quality480);
                                            result.add(videoDbModel);
                                        }
                                );
                            }
                    );
                }
        );
        return result;
    }

    private List<String> getIdsFromDownloadsAnime() {
        List<String> result = new ArrayList<>();
        Path dir = Paths.get("D:/videos/");
        try {
            Files.walk(dir).forEach(path -> result.add(path.toFile().getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @GetMapping
    public void main() throws IOException, URISyntaxException, InterruptedException {

        List<String> animeNames = getIdsFromDownloadsAnime();

        animeNames.forEach((id) -> {
            try {
                loadAnime(Integer.parseInt(id));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
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
    public ResponseEntity<?> getAnimeById(
            @RequestParam("anime_id") Long animeId,
            @RequestParam("user_id") Long userId
    ) {
        return animeService.getAnimeById(animeId, userId);
    }

    @PostMapping("/id/favourite")
    public ResponseEntity<String> setAnimeFavouriteStatus(@RequestBody FavouriteAnimeRequest favouriteAnimeRequest) {
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
