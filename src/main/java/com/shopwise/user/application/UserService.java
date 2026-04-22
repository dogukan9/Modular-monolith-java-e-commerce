package com.shopwise.user.application;

import com.shopwise.shared.exception.BusinessException;
import com.shopwise.user.application.dto.CreateUserRequest;
import com.shopwise.user.application.dto.UpdateUserRequest;
import com.shopwise.user.application.dto.UserResponse;
import com.shopwise.user.domain.User;
import com.shopwise.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser(CreateUserRequest request) {

         if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS",
                    "Bu email zaten kullanılıyor: " + request.email());
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user = User.create(request.email(), hashedPassword, request.fullName());

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return userMapper.toResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);
        user.updateFullName(request.fullName());
        return userMapper.toResponse(userRepository.save(user));
    }

    public void deactivateUser(Long id) {
        User user = findUserById(id);
        user.deactivate();
        userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "USER_NOT_FOUND",
                        "Kullanıcı bulunamadı: " + id));
    }
}