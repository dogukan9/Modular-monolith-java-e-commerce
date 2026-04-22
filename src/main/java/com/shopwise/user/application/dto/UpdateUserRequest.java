package com.shopwise.user.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank(message = "Ad soyad boş olamaz")
        String fullName
) {}