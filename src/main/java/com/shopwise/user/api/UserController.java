package com.shopwise.user.api;

import com.shopwise.shared.api.ApiResponse;
import com.shopwise.shared.api.PageResponse;
import com.shopwise.user.application.UserService;
import com.shopwise.user.application.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Kullanıcı getirildi"));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @ModelAttribute UserFilterRequest filter) {
        PageResponse<UserResponse> response = userService.getUsers(filter);
        return ResponseEntity.ok(ApiResponse.success(response,
                response.getTotalElements() + " kullanıcı bulundu"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserRequest request) {

        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success(response,
                "Kullanıcı güncellendi"));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivateUser(
            @PathVariable Long id) {

        userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Kullanıcı deaktive edildi"));
    }
}

