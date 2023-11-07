package com.miraelDev.demo.models.responseDto;

import com.miraelDev.demo.models.dbModels.GenreDbModel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GenreResponseDto {
    private String name;
    private String russian;

    @Builder
    public GenreResponseDto(
            String name,
            String russian
    ) {
        this.name = name;
        this.russian = russian;
    }



    public static List<GenreResponseDto> toDbModelList(List<GenreDbModel> dbModels) {
        List<GenreResponseDto> dbModelList = new ArrayList<GenreResponseDto>();
        for (GenreDbModel dbModel : dbModels) {
            dbModelList.add(toDbModel(dbModel));
        }
        return dbModelList;
    }

    private static GenreResponseDto toDbModel(GenreDbModel dbModel) {
        return new GenreResponseDto(dbModel.getName(), dbModel.getRussian());
    }
}
