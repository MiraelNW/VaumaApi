package com.miraelDev.demo.models.responseDto;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageResponseDto {
    private String original;
    private String preview;

    @Builder
    public ImageResponseDto(
            String original,
            String preview
    ) {
        this.original = original;
        this.preview = preview;
    }
}
