package com.miraelDev.demo.models.dto;

import com.miraelDev.demo.models.dbModels.GenreDbModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GenreDto {
    private String name;
    private String russian;

    public static List<GenreDbModel> toDbModelList(List<GenreDto> dtos) {
        List<GenreDbModel> dbModelList = new ArrayList<GenreDbModel>();
        for (GenreDto dto : dtos) {
            dbModelList.add(toDbModel(dto));
        }
        return dbModelList;
    }

    private static GenreDbModel toDbModel(GenreDto dto) {
        return new GenreDbModel(dto.getName(), dto.getRussian());
    }
}
