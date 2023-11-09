package com.miraelDev.demo.models.dbModels;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@EqualsAndHashCode
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SimilarAnimeDbModel {
    @Id
    private Integer id;
    private String name;
    private String russian;
    @Embedded
    private ImageDbModel image;
    private String kind;
    private Float score;
    private String rating;

    @ManyToMany(mappedBy = "similar")
    private Set<AnimeDbModel> animeSet = new HashSet<AnimeDbModel>();

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
