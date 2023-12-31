package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.responseDto.PagingAnimeResponseDto;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.repositories.anime.SearchAnimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class SearchService {

    @Autowired
    SearchAnimeRepository searchAnimeRepo;


    public ResponseEntity<PagingResponseDto> searchAnime(
            String name,
            String genreCode,
            String dateCode,
            String sortCode,
            Integer page,
            Integer pageSize
    ) {
        System.out.println(name);
        System.out.println(parseGenreCode(genreCode));
        System.out.println(parseYearCode(dateCode));
        System.out.println(parseSortCode(sortCode));

        Slice<AnimeDbModel> result = searchAnimeRepo
                .findAll(
                        AnimeSearchSpecs
                                .isNameContains(name)
                                .or(AnimeSearchSpecs.isRussianContains(name))
                                .and(AnimeSearchSpecs.isGenreContains(parseGenreCode(genreCode)))
                                .and(AnimeSearchSpecs.betweenYears(parseYearCode(dateCode))),
                        PageRequest.of(page, pageSize, parseSortCode(sortCode))
                );


        return ResponseEntity.ok(
                PagingResponseDto.builder()
                        .animeResponseDtoList(PagingAnimeResponseDto.toDtoModelList(result.getContent()))
                        .isLast(result.isLast())
                        .build()
        );
    }

    private Map<String, Date> parseYearCode(String yearCode) {
        if (yearCode == null) return null;
        char[] yearCodeCharArray = yearCode.toCharArray();
        Map<String, Date> resultList = new HashMap<>();
        for (char ch : yearCodeCharArray) {
            switch (ch) {
                case 'a' -> {
                    resultList.put("from", Date.valueOf(LocalDate.of(1980, 1, 31)));
                    resultList.put("to", Date.valueOf(LocalDate.of(1999, 1, 31)));
                }
                case 'b' -> {
                    resultList.put("from", Date.valueOf(LocalDate.of(2000, 1, 31)));
                    resultList.put("to", Date.valueOf(LocalDate.of(2007, 1, 31)));
                }
                case 'c' -> {
                    resultList.put("from", Date.valueOf(LocalDate.of(2008, 1, 31)));
                    resultList.put("to", Date.valueOf(LocalDate.of(2014, 1, 31)));
                }
                case 'd' -> {
                    resultList.put("from", Date.valueOf(LocalDate.of(2015, 1, 31)));
                    resultList.put("to", Date.valueOf(LocalDate.of(2020, 1, 31)));
                }
                case 'e' -> {
                    resultList.put("from", Date.valueOf(LocalDate.of(2021, 1, 31)));
                    resultList.put("to", Date.valueOf(LocalDate.of(2021, 12, 31)));
                }
                case 'f' -> {
                    resultList.put("from", Date.valueOf(LocalDate.of(2022, 1, 31)));
                    resultList.put("to", Date.valueOf(LocalDate.of(2022, 12, 31)));
                }
                case 'g' -> {
                    resultList.put("from", Date.valueOf(LocalDate.of(2023, 1, 31)));
                    resultList.put("to", Date.valueOf(LocalDate.of(2023, 12, 31)));
                }
            }
        }
        return resultList;
    }

    private Sort parseSortCode(String sort) {
        if (sort == null) return Sort.by("releasedOn", "score").descending();
        Sort result;
        switch (sort) {
            case "name" -> {
                result = Sort.by("name").ascending();
            }
            case "popular" -> {
                result = Sort.by("score").descending();
            }
            case "series" -> {
                result = Sort.by("episodes").ascending();
            }
            case "new" -> {
                result = Sort.by("releasedOn").descending();
            }
            default -> {
                result = Sort.by("releasedOn", "score").descending();
            }
        }
        return result;
    }

    private Set<String> parseGenreCode(String genreCode) {
        if (genreCode == null) return null;
        char[] genreCodeCharArray = genreCode.toCharArray();
        Set<String> resultList = new HashSet<>();
        for (char ch : genreCodeCharArray) {
            switch (ch) {
                case 'a' -> {
                    resultList.add("Сенен");
                }
                case 'b' -> {
                    resultList.add("Седзе");
                }
                case 'c' -> {
                    resultList.add("Комедия");
                }
                case 'd' -> {
                    resultList.add("Романтика");
                }
                case 'e' -> {
                    resultList.add("Школа");
                }
                case 'f' -> {
                    resultList.add("Боевые искусства");
                }
                case 'g' -> {
                    resultList.add("Гарем");
                }
                case 'h' -> {
                    resultList.add("Детектив");
                }
                case 'i' -> {
                    resultList.add("Драма");
                }
                case 'j' -> {
                    resultList.add("Повседневность");
                }
                case 'k' -> {
                    resultList.add("Приключение");
                }
                case 'l' -> {
                    resultList.add("Психологическое");
                }
                case 'm' -> {
                    resultList.add("Сверхъестественное");
                }
                case 'n' -> {
                    resultList.add("Спорт");
                }
                case 'o' -> {
                    resultList.add("Ужасы");
                }
                case 'p' -> {
                    resultList.add("Фантастика");
                }
                case 'q' -> {
                    resultList.add("Фэнтези");
                }
                case 'r' -> {
                    resultList.add("Экшен");
                }
                case 's' -> {
                    resultList.add("Триллер");
                }
                case 't' -> {
                    resultList.add("Супер сила");
                }
                case 'u' -> {
                    resultList.add("Гурман");
                }
            }
        }
        return resultList;
    }


}
