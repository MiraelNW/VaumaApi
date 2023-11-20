package com.miraelDev.demo.models.responseDto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PagingResponseDto {

    private Boolean isLast;
    private List<AnimeResponseDto> animeResponseDtoList;

    @Builder
    public PagingResponseDto(
            List<AnimeResponseDto> animeResponseDtoList,
            Boolean isLast
    ) {
        this.animeResponseDtoList = animeResponseDtoList;
        this.isLast = isLast;
    }

}
