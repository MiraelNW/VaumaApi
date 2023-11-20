package com.miraelDev.demo.payload;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpModel {
    private String username;
    private String email;
    private String password;
    private MultipartFile userImage;
    private String otpToken;
}