package com.miraelDev.demo.models.responseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoDto {

    private Long id;
    private String username;
    private String email;
    private String image;
    private String name;

    public UserInfoDto(
            Long id,
            String name,
            String username,
            String email,
            String imagePath
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.image = imagePath;
    }
}