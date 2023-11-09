package com.miraelDev.demo.models.responseDto;

import com.miraelDev.demo.models.dbModels.SimilarAnimeDbModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
public class SimilarResponseDto {

    private Integer id;
    private String name;
    private String russian;
    private ImageResponseDto image;
    private String kind;
    private Float score;
    private String rating;

    @Builder
    public SimilarResponseDto(
            Integer id,
            String name,
            String russian,
            ImageResponseDto image,
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

    public static List<SimilarResponseDto> toDbModelList(Set<SimilarAnimeDbModel> dbModels) {
        List<SimilarResponseDto> dbModelList = new ArrayList<SimilarResponseDto>();
        for (SimilarAnimeDbModel dbModel : dbModels) {
            dbModelList.add(toDbModel(dbModel));
        }
        return dbModelList;
    }

    private static SimilarResponseDto toDbModel(SimilarAnimeDbModel dbModel) {
        return SimilarResponseDto
                .builder()
                .id(dbModel.getId())
                .name(dbModel.getName())
                .russian(dbModel.getRussian())
                .image(
                        ImageResponseDto.builder()
                                .original("http://localhost:8080/animes/images/original/" + dbModel.getId())
                                .preview("http://localhost:8080/animes/images/preview/" + dbModel.getId())
                                .build()
                )
                .kind(dbModel.getKind())
                .score(dbModel.getScore())
                .kind(dbModel.getKind())
                .build();
    }

}
