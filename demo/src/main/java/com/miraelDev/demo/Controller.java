package com.miraelDev.demo;


import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.dbModels.ImageDbModel;
import com.miraelDev.demo.models.dto.AnimeInfoDto;
import com.miraelDev.demo.models.dto.GenreDto;
import com.miraelDev.demo.models.dto.SimilarAnimeDto;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.servises.AnimeService;
import com.miraelDev.demo.servises.PagingService;
import com.miraelDev.demo.servises.SearchService;
import com.miraelDev.demo.shikimory.ApiFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
public class Controller {

    @Autowired
    public PagingService pagingService;

    @Autowired
    public AnimeService animeService;

    @Autowired
    public SearchService searchService;

    @GetMapping
    public void main() {
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            for (int i = 1; i < 100; i++) {

                try {
                    AnimeInfoDto dto = ApiFactory.apiService.getAnime(i).execute().body();
                    List<SimilarAnimeDto> similarDto = ApiFactory.apiService.getSimilarAnime(i).execute().body();
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
                                .genres(GenreDto.toDbModelList(dto.getGenres()))
                                .similar(SimilarAnimeDto.toDbModelList(similarDto))
                                .build();

                        downloadFiles("https://shikimori.one/" + dto.getImage().getPreview(), "C:\\Users\\1\\Desktop\\animes\\preview\\" + dbModel.getId() + ".png", 1);
                        downloadFiles("https://shikimori.one/" + dto.getImage().getOriginal(), "C:\\Users\\1\\Desktop\\animes\\original\\" + dbModel.getId() + ".png", 1);
                        dbModel.setImage(
                                ImageDbModel.builder()
                                        .original("http://localhost:8080/animes/images/original/" + dbModel.getId())
                                        .preview("http://localhost:8080/animes/images/preview/" + dbModel.getId())
                                        .build()
                        );
                        animeService.save(dbModel);
                    }

                } catch (IOException e) {
                    continue;
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/anime/new")
    public PagingResponseDto getNewAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getNewAnime(page, pageSize);
    }

    @GetMapping("/anime/popular")
    public PagingResponseDto getPopularAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getPopularAnime(page, pageSize);
    }

    @GetMapping("/anime/name")
    public PagingResponseDto getNameAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getNameAnime(page, pageSize);
    }

    @GetMapping("/anime/film")
    public PagingResponseDto getFilmAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getFilmAnime(page, pageSize);
    }

    @GetMapping("/anime/{id}")
    public AnimeDbModel getAnimeById(@PathVariable Long id) {
        return animeService.getUser(id);
    }

    @GetMapping("/anime/search")
    public PagingResponseDto searchAnime(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "genres", required = false) String genreCode,
            @RequestParam(value = "date", required = false) String dateCode,
            @RequestParam(value = "sort", required = false) String sortCode,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "page_size") Integer pageSize
    ) {
        System.out.println(name);
        return searchService.searchAnime(
                name,
                genreCode,
                dateCode,
                sortCode,
                page,
                pageSize
        );
    }

    @GetMapping(value = "/animes/images/original/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource getOriginalImageById(@PathVariable Long id) throws IOException {
        return animeService.getOriginalImage(id);
    }

    @GetMapping(value = "/animes/images/preview/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public Resource getPreviewImageById(@PathVariable Long id) throws IOException {
        return animeService.getPreviewImage(id);
    }


    public static void downloadFiles(String strURL, String strPath, int buffSize) {
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


}
