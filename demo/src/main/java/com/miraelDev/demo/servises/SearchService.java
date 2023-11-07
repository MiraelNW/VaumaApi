package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.responseDto.AnimeResponseDto;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.repositories.SearchAnimeRepo;
import kotlin.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class SearchService {

    @Autowired
    SearchAnimeRepo searchAnimeRepo;


    public PagingResponseDto searchAnime(
            String name,
            String genreCode,
            String dateCode,
            String sortCode,
            Integer page,
            Integer pageSize
    ) {

//        if (name != null) {
        Slice<AnimeDbModel> resultNameContaining = searchAnimeRepo.findByNameContainingIgnoreCase(name, PageRequest.of(page, pageSize));
        Slice<AnimeDbModel> resultNameStartingWith = searchAnimeRepo.findByNameStartingWithIgnoreCase(name, PageRequest.of(page, pageSize));
        Slice<AnimeDbModel> resultRussianStartingWith = searchAnimeRepo.findByRussianStartingWithIgnoreCase(name, PageRequest.of(page, pageSize));
        Slice<AnimeDbModel> resultRussianContaining = searchAnimeRepo.findByRussianContainingIgnoreCase(name, PageRequest.of(page, pageSize));

        Set<AnimeDbModel> mergeList = new LinkedHashSet<AnimeDbModel>();

        mergeList.addAll(resultNameStartingWith.getContent());
        mergeList.addAll(resultNameContaining.getContent());
        mergeList.addAll(resultRussianStartingWith.getContent());
        mergeList.addAll(resultRussianContaining.getContent());

        return PagingResponseDto
                .builder()
                .animeResponseDtoList(AnimeResponseDto.toDbModelList(mergeList))
                .build();
//        }
    }

    private List<Pair<Integer, Integer>> parseYearCode(String yearCode) {
        char[] yearCodeCharArray = yearCode.toCharArray();
        List<Pair<Integer, Integer>> resultList = new ArrayList<Pair<Integer, Integer>>();
        for (char ch : yearCodeCharArray) {
            switch (ch) {
                case 'a': {
                    resultList.add(new Pair(1980, 1999));
                }
                case 'b': {
                    resultList.add(new Pair(2000, 2007));
                }
                case 'c': {
                    resultList.add(new Pair(2008, 2014));
                }
                case 'd': {
                    resultList.add(new Pair(2015, 2020));
                }
                case 'e': {
                    resultList.add(new Pair(2021, 2021));
                }
                case 'f': {
                    resultList.add(new Pair(2022, 2022));
                }
                case 'g': {
                    resultList.add(new Pair(2023, 2023));
                }
            }
        }
        return resultList;
    }


}
