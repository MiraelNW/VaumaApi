package com.miraelDev.demo.models.dto;

import lombok.Data;

import java.util.List;


@Data
public class AnimeInfoDto {

    private Integer id;
    private String name;
    private String russian;
    private String multi_name;
    private String multi_rus;
    private ImageDto image;
    private String kind;
    private Float score;
    private String status;
    private Integer episodes;
    private Integer episodes_aired;
    private String description;
    private String description_rus;
    private String aired_on;
    private String aired_year;
    private String released_on;
    private String rating;
    private Integer duration;
    private String franchise;
    private Boolean favoured;
    private List<GenreDto> genres;

}
