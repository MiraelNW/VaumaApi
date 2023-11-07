package com.miraelDev.demo.models.dbModels;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class AnimeDbModel implements Serializable {

    @Id
    private Integer id;
    private String name;
    private String russian;
    private String multi_name;
    private String multi_rus;
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
    @ElementCollection
    @CollectionTable(name = "genres_model")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<GenreDbModel> genres;

    @ElementCollection
    @CollectionTable(name = "similar_model")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private List<SimilarAnimeDbModel> similar;

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
            List<GenreDbModel> genres,
            List<SimilarAnimeDbModel> similar
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