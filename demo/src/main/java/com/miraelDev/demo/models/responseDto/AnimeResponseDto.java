package com.miraelDev.demo.models.responseDto;

import com.miraelDev.demo.models.dbModels.AnimeDbModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class AnimeResponseDto {

    private Integer id;
    private String name;
    private String russian;
    private ImageResponseDto image;
    private String kind;
    private Float score;
    private String status;
    private Integer episodes;
    private Integer episodes_aired;
    private String description;
    private String description_rus;
    private String aired_on;
    private String released_on;
    private String rating;
    private Integer duration;
    private Boolean favoured;

    private Boolean is_favourite;
    private List<GenreResponseDto> genres;
    private List<SimilarResponseDto> similar;

    @Builder
    public AnimeResponseDto(
            Integer id,
            String name,
            String russian,
            ImageResponseDto image,
            String kind,
            Float score,
            String status,
            Integer episodes,
            Integer episodes_aired,
            String description,
            String description_rus,
            String aired_on,
            String released_on,
            String rating,
            Integer duration,
            Boolean favoured,
            List<GenreResponseDto> genres,
            List<SimilarResponseDto> similar,
            Boolean isFavourite
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
        this.aired_on = aired_on;
        this.released_on = released_on;
        this.rating = rating;
        this.duration = duration;
        this.favoured = favoured;
        this.genres = genres;
        this.similar = similar;
        this.is_favourite = isFavourite;
    }

    public static List<AnimeResponseDto> toDtoModelList(Collection<AnimeDbModel> dbModels, Boolean isFavourite) {
        List<AnimeResponseDto> animeResponseDtoList = new ArrayList<AnimeResponseDto>();
        for (AnimeDbModel dbModel : dbModels) {
            animeResponseDtoList.add(toDtoModel(dbModel, isFavourite));
        }
        return animeResponseDtoList;
    }

    public static AnimeResponseDto toDtoModel(AnimeDbModel dbModel, boolean isFavourite) {
        return new AnimeResponseDto(
                dbModel.getId(),
                dbModel.getName(),
                dbModel.getRussian(),
                ImageResponseDto.builder()
                        .original("http://10.0.2.2:8080/api/v1/anime/images/original/" + dbModel.getId())
                        .preview("http://10.0.2.2:8080/api/v1/anime/images/preview/" + dbModel.getId())
                        .build(),
                dbModel.getKind(),
                dbModel.getScore(),
                dbModel.getStatus(),
                dbModel.getEpisodes(),
                dbModel.getEpisodes_aired(),
                dbModel.getDescription(),
                dbModel.getDescription_rus(),
                dbModel.getAiredOn(),
                dbModel.getReleasedOn().toString(),
                dbModel.getRating(),
                dbModel.getDuration(),
                dbModel.getFavoured(),
                GenreResponseDto.toDbModelList(dbModel.getGenres()),
                SimilarResponseDto.toDbModelList(dbModel.getSimilar()),
                isFavourite
        );
    }
}
