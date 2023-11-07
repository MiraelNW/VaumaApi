package com.miraelDev.demo.models.dbModels;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class GenreDbModel {
    private String name;
    private String russian;

    @Builder
    public GenreDbModel(
            String name,
            String russian
    ) {
        this.name = name;
        this.russian = russian;
    }
}
