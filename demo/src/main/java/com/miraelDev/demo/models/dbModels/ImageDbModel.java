package com.miraelDev.demo.models.dbModels;


import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
public class ImageDbModel {
    private String original;
    private String preview;

    @Builder
    public ImageDbModel(
            String original,
            String preview
    ) {
        this.original = original;
        this.preview = preview;
    }
}
