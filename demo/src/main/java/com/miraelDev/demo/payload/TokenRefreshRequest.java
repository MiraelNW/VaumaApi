package com.miraelDev.demo.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TokenRefreshRequest {
    private String refreshToken;
}