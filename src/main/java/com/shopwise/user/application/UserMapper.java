package com.shopwise.user.application;

import com.shopwise.shared.dto.AuditUserInfo;
import com.shopwise.shared.port.UserLookupPort;
import com.shopwise.user.application.dto.UserResponse;
import com.shopwise.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final UserLookupPort userLookupPort;

    public UserResponse toResponse(User user,
                                   AuditUserInfo createdBy,
                                   AuditUserInfo updatedBy) {


        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .build();
    }
}