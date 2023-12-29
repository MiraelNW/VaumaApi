package com.miraelDev.demo.models.dbModels;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
public class VideoDbModel {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private String videoImage;
    private String videoName;
    private String videoUrl480;
    private String videoUrl720;
    private String videoUrl1080;

    @ManyToOne
    @JoinColumn(name = "anime_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private AnimeDbModel anime;

    @Builder
    public VideoDbModel(
            String videoImage,
            String videoName,
            String videoUrl480,
            String videoUrl720,
            String videoUrl1080
    ) {
        this.videoName = videoName;
        this.videoImage = videoImage;
        this.videoUrl480 = videoUrl480;
        this.videoUrl720 = videoUrl720;
        this.videoUrl1080 = videoUrl1080;
    }
}
