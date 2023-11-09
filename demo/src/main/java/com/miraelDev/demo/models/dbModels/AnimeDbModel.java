package com.miraelDev.demo.models.dbModels;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@Entity
public class AnimeDbModel implements Serializable {

    @Id
    private Integer id;
    private String name;
    private String russian;
    @Embedded
    private ImageDbModel image;
    private String kind;
    private Float score;
    private String status;
    private Integer episodes;
    private Integer episodes_aired;
    @Column(columnDefinition = "text")
    private String description;
    private String description_rus;
    private String airedOn;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(columnDefinition = "date")
    private Date releasedOn;
    private String rating;
    private Integer duration;
    private Boolean favoured;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "genre_model",
            joinColumns = @JoinColumn(name = "anime_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    @EqualsAndHashCode.Exclude private Set<GenreDbModel> genres = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "similar_model",
            joinColumns = @JoinColumn(name = "anime_id"),
            inverseJoinColumns = @JoinColumn(name = "similar_id"))
    @EqualsAndHashCode.Exclude private Set<SimilarAnimeDbModel> similar = new HashSet<>();

    @Builder
    public AnimeDbModel(
            Integer id,
            String name,
            String russian,
            ImageDbModel image,
            String kind,
            Float score,
            String status,
            Integer episodes,
            Integer episodes_aired,
            String description,
            String description_rus,
            String airedOn,
            Date releasedOn,
            String rating,
            Integer duration,
            Boolean favoured,
            Set<GenreDbModel> genres,
            Set<SimilarAnimeDbModel> similar
    ) {
        this.id = id;
        this.name = name;
        this.russian = russian;
        this.kind = kind;
        this.score = score;
        this.image = image;
        this.status = status;
        this.episodes = episodes;
        this.episodes_aired = episodes_aired;
        this.description = description;
        this.description_rus = description_rus;
        this.airedOn = airedOn;
        this.releasedOn = releasedOn;
        this.rating = rating;
        this.duration = duration;
        this.favoured = favoured;
        this.genres = genres;
        this.similar = similar;
    }

}