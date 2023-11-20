package com.miraelDev.demo.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class SignInModel {
    private final String password;
    private final String username;
}