package com.miraelDev.demo.models.dto;

import com.miraelDev.demo.models.dbModels.GenreDbModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class GenreDto {
    private Integer id;
    private String name;
    private String russian;

    public static Set<GenreDbModel> toDbModelList(List<GenreDto> dtos) {
        Set<GenreDbModel> dbModelList = new HashSet<GenreDbModel>();
        for (GenreDto dto : dtos) {
            dbModelList.add(toDbModel(dto));
        }
        return dbModelList;
    }

    private static GenreDbModel toDbModel(GenreDto dto) {
        return new GenreDbModel(dto.getId(), dto.getName(), dto.getRussian());
    }
}
