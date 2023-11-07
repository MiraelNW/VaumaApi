package com.miraelDev.demo.models.dto;

import com.miraelDev.demo.models.dbModels.ImageDbModel;
import com.miraelDev.demo.models.dbModels.SimilarAnimeDbModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class SimilarAnimeDto {

    private Integer id;
    private String name;
    private String russian;
    private ImageDbModel image;
    private String kind;
    private Float score;
    private String rating;


    public static List<SimilarAnimeDbModel> toDbModelList(List<SimilarAnimeDto> dtos) {
        List<SimilarAnimeDbModel> dbModelList = new ArrayList<SimilarAnimeDbModel>();
        for (SimilarAnimeDto dto : dtos) {
            dbModelList.add(toDbModel(dto));
        }
        return dbModelList;
    }

    private static SimilarAnimeDbModel toDbModel(SimilarAnimeDto dto) {
        return new SimilarAnimeDbModel(
                dto.getId(),
                dto.getName(),
                dto.getRussian(),
                ImageDbModel.builder()
                        .original("http://localhost:8080/animes/images/original/" + dto.getId())
                        .preview("http://localhost:8080/animes/images/preview/" + dto.getId())
                        .build(),
                dto.getKind(),
                dto.getScore(),
                dto.getRating()
        );
    }

}
