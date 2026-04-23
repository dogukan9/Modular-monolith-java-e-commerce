package com.shopwise.user.application.dto;

import com.shopwise.user.domain.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UserFilterRequest {

    private String email;
    private String fullName;
    private UserRole role;
    private Boolean active;

    private LocalDateTime createdAtStart;
    private LocalDateTime createdAtEnd;

    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}