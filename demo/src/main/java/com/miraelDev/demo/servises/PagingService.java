package com.miraelDev.demo.servises;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import com.miraelDev.demo.models.responseDto.PagingAnimeResponseDto;
import com.miraelDev.demo.models.responseDto.PagingResponseDto;
import com.miraelDev.demo.repositories.anime.PagingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PagingService {

    @Autowired
    PagingRepository pagingRepo;

//    public ResponseEntity<PagingResponseDto> getAnime() {
//        List<AnimeDbModel> result = pagingRepo.findAll();
//        return ResponseEntity.ok(
//                PagingResponseDto
//                        .builder()
//                        .animeResponseDtoList(PagingAnimeResponseDto.toDtoModelList(result))
//                        .build()
//        );
//    }


    public ResponseEntity<PagingResponseDto> getNewAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("airedOn").descending()));
        return ResponseEntity.ok(
                PagingResponseDto.builder()
                        .animeResponseDtoList(PagingAnimeResponseDto.toDtoModelList(result.getContent()))
                        .isLast(result.isLast())
                        .build()
        );


    }

    public ResponseEntity<PagingResponseDto> getPopularAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("score").ascending()));
        return ResponseEntity.ok(
                PagingResponseDto.builder()
                        .animeResponseDtoList(PagingAnimeResponseDto.toDtoModelList(result.getContent()))
                        .isLast(result.isLast())
                        .build()
        );
    }

    public ResponseEntity<PagingResponseDto> getNameAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("name").descending()));
        return ResponseEntity.ok(
                PagingResponseDto.builder()
                        .animeResponseDtoList(PagingAnimeResponseDto.toDtoModelList(result.getContent()))
                        .isLast(result.isLast())
                        .build()
        );
    }

    public ResponseEntity<PagingResponseDto> getFilmAnime(Integer page, Integer pageSize) {
        Slice<AnimeDbModel> result = pagingRepo.findAllBy(PageRequest.of(page, pageSize, Sort.by("kind").ascending()));
        return ResponseEntity.ok(
                PagingResponseDto.builder()
                        .animeResponseDtoList(PagingAnimeResponseDto.toDtoModelList(result.getContent()))
                        .isLast(result.isLast())
                        .build()
        );
    }


}
