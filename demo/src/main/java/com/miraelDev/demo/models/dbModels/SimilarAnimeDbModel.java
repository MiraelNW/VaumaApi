package com.miraelDev.demo.models.dbModels;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Embeddable
public class SimilarAnimeDbModel {

    private Integer id;
    private String name;
    private String russian;
    @Embedded
    private ImageDbModel image;
    private String kind;
    private Float score;
    private String rating;

    @Builder
    public SimilarAnimeDbModel(
            Integer id,
            String name,
            String russian,
            ImageDbModel image,
            String kind,
            Float score,
            String rating
    ) {
        this.id = id;
        this.name = name;
        this.russian = russian;
        this.image = image;
        this.kind = kind;
        this.score = score;
        this.rating = rating;
    }

}
