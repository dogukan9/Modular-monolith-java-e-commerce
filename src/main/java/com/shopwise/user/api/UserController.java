package com.shopwise.user.api;


import com.shopwise.shared.api.ApiResponse;
import com.shopwise.user.application.UserService;
import com.shopwise.user.application.dto.CreateUserRequest;
import com.shopwise.user.application.dto.UpdateUserRequest;
import com.shopwise.user.application.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @RequestBody @Valid CreateUserRequest request) {

        log.info("Create user request: {}", request.email());
        UserResponse response = userService.createUser(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Kullanıcı başarıyla oluşturuldu"));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Kullanıcı getirildi"));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(response,
                response.size() + " kullanıcı getirildi"));
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

