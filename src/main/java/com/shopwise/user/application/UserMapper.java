package com.shopwise.user.application;

import com.shopwise.user.application.dto.UserResponse;
import com.shopwise.user.domain.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(UserResponse.AuditUser.builder()
                        .id(user.getCreatedBy())
                        .build())
                .updatedBy(UserResponse.AuditUser.builder()
                        .id(user.getUpdatedBy())
                        .build())
                .build();
    }
}
