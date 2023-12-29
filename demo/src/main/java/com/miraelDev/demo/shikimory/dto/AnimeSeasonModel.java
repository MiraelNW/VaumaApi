package com.miraelDev.demo.shikimory.dto;

import lombok.*;

import java.util.List;


@Data
@ToString
@Setter
@Getter
@NoArgsConstructor
public class AnimeSeasonModel {

    private List<String> seasonLinks;
    private List<String> filmsLinks;
    private List<String> namesForGraphQl;
    private List<String> filmsNameForGraphQl;

}
