package com.shopwise.user.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private String email;
    private String fullName;
    private String role;
}