package com.miraelDev.demo.models.responseDto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PagingResponseDto {

    private List<AnimeResponseDto> animeResponseDtoList;

    @Builder
    public PagingResponseDto(
            List<AnimeResponseDto> animeResponseDtoList
    ) {
        this.animeResponseDtoList = animeResponseDtoList;
    }

}
