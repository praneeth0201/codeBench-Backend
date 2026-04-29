package com.codeBench.demo.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.management.ConstructorParameters;

@Getter
@Setter

public class LoginResponse {

    private String message;
    private String username;
    private String accessToken;
    private String refreshToken;

    public LoginResponse(String message, String username,
                         String accessToken, String refreshToken) {
        this.message = message;
        this.username = username;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}