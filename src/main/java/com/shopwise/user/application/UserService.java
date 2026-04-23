package com.shopwise.user.application;

import com.shopwise.shared.exception.BusinessException;
import com.shopwise.user.application.dto.*;
import com.shopwise.user.domain.User;
import com.shopwise.user.infrastructure.UserRepository;
import com.shopwise.user.infrastructure.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
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
    public PageResponse<UserResponse> getUsers(UserFilterRequest filter) {
        Sort.Direction direction = filter.getSortDir().equalsIgnoreCase("asc")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;


        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(direction, filter.getSortBy())
        );

        Specification<User> spec = UserSpecification.build(filter);
        Page<User> userPage = userRepository.findAll(spec, pageable);

        Page<UserResponse> responsePage = userPage.map(userMapper::toResponse);
        return PageResponse.of(responsePage);
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