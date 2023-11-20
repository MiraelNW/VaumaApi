package com.miraelDev.demo;


import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.dbModels.GenreDbModel;
import com.miraelDev.demo.models.dbModels.ImageDbModel;
import com.miraelDev.demo.models.dbModels.SimilarAnimeDbModel;
import com.miraelDev.demo.models.responseDto.AnimeResponseDto;
import com.miraelDev.demo.shikimory.dto.AnimeInfoDto;
import com.miraelDev.demo.shikimory.dto.GenreDto;
import com.miraelDev.demo.shikimory.dto.SimilarAnimeDto;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.servises.*;
import com.miraelDev.demo.shikimory.ApiFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("api/v1/anime")
public class CommonController {

    @Autowired
    private PagingService pagingService;
    @Autowired
    private AnimeService animeService;
    @Autowired
    private GenreService genreService;
    @Autowired
    private SimilarAnimeService similarAnimeService;
    @Autowired
    private SearchService searchService;

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
                                        .original("http://10.0.2.2:8080/api/v1/anime/images/original/" + dbModel.getId())
                                        .preview("http://10.0.2.2:8080/api/v1/anime/images/preview/" + dbModel.getId())
                                        .build()
                        );
                        genreService.saveAll(genreDbModelSet);
                        similarAnimeService.saveAll(similarAnimeDbModels);
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
    public PagingResponseDto getNewAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getNewAnime(page, pageSize);
    }

    @GetMapping("/popular")
    public PagingResponseDto getPopularAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getPopularAnime(page, pageSize);
    }

    @GetMapping("/name")
    public PagingResponseDto getNameAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getNameAnime(page, pageSize);
    }

    @GetMapping("/film")
    public PagingResponseDto getFilmAnime(@RequestParam("page") Integer page, @RequestParam("page_size") Integer pageSize) {
        return pagingService.getFilmAnime(page, pageSize);
    }

    @GetMapping("/{id}")
    public AnimeResponseDto getAnimeById(@PathVariable Long id) {
        return animeService.getUser(id);
    }

    @GetMapping("/search")
    public PagingResponseDto searchAnime(
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
