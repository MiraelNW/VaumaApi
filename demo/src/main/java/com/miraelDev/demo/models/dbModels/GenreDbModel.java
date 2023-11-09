package com.miraelDev.demo.models.dbModels;

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
public class GenreDbModel {

    @Id
    private Integer id;
    private String genreName;
    private String genreRussian;

    @ManyToMany(mappedBy = "genres")
    private Set<AnimeDbModel> animeSet = new HashSet<AnimeDbModel>();

    @Builder
    public GenreDbModel(
            Integer id,
            String name,
            String russian
    ) {
        this.id = id;
        this.genreName = name;
        this.genreRussian = russian;
    }
}
