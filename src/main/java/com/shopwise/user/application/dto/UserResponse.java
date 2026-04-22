package com.shopwise.user.application.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String fullName;
    private String role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AuditUser createdBy;
    private AuditUser updatedBy;

    @Getter
    @Builder
    public static class AuditUser {
        private Long id;
        private String fullName;
    }
}