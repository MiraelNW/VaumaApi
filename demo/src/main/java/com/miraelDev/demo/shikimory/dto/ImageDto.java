package com.miraelDev.demo.shikimory.dto;

import lombok.Data;

@Data
public class ImageDto {
    private String original;
    private String preview;

//    public static ImageDbModel toDbModel(ImageDto dto) {
//        return new ImageDbModel(dto.getOriginal(), dto.getPreview());
//    }
}
