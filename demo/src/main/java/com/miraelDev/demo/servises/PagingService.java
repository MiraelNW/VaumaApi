package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.responseDto.AnimeResponseDto;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.repositories.PagingRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PagingService {

    @Autowired
    PagingRepo pagingRepo;


    public PagingResponseDto getNewAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("airedOn").descending()));
        return PagingResponseDto
                .builder()
                .animeResponseDtoList(AnimeResponseDto.toDbModelList(result.getContent()))
                .build();
    }

    public PagingResponseDto getPopularAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("score").ascending()));
        return PagingResponseDto
                .builder()
                .animeResponseDtoList(AnimeResponseDto.toDbModelList(result.getContent()))
                .build();
    }

    public PagingResponseDto getNameAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("name").descending()));
        return PagingResponseDto
                .builder()
                .animeResponseDtoList(AnimeResponseDto.toDbModelList(result.getContent()))
                .build();
    }

    public PagingResponseDto getFilmAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("kind").ascending()));
        return PagingResponseDto
                .builder()
                .animeResponseDtoList(AnimeResponseDto.toDbModelList(result.getContent()))
                .build();
    }


}
