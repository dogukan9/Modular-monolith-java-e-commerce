package com.shopwise.user.application;

import com.shopwise.shared.dto.AuditUserInfo;
import com.shopwise.shared.exception.BusinessException;
import com.shopwise.shared.port.UserLookupPort;
import com.shopwise.shared.security.JwtService;
import com.shopwise.user.application.dto.CreateUserRequest;
import com.shopwise.user.application.dto.LoginRequest;
import com.shopwise.user.application.dto.LoginResponse;
import com.shopwise.user.application.dto.UserResponse;
import com.shopwise.user.domain.User;
import com.shopwise.user.domain.event.UserLoggedInEvent;
import com.shopwise.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserLookupPort userLookupPort;
    private final UserCacheService userCacheService;
    private final ApplicationEventPublisher eventPublisher;


    public LoginResponse login(LoginRequest request) {

        // 1. Kullanıcı var mı?
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(
                        "INVALID_CREDENTIALS", "Email veya şifre hatalı"));

        // 2. Aktif mi?
        if (!user.isActive()) {
            throw new BusinessException("USER_INACTIVE",
                    "Hesabınız aktif değil");
        }

        // 3. Şifre doğru mu?
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BusinessException("INVALID_CREDENTIALS",
                    "Email veya şifre hatalı");
        }

        // 4. Token üret
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtService.generateToken(userDetails);

        log.info("Login successful: {}", user.getEmail());
        userCacheService.addToWhitelist(user.getId());
        eventPublisher.publishEvent(new UserLoggedInEvent(user.getId()));


        return LoginResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }

    public UserResponse register(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("EMAIL_ALREADY_EXISTS",
                    "Bu email zaten kullanılıyor");
        }
        String hashedPassword = passwordEncoder.encode(request.password());
        User user = User.create(request.email(), hashedPassword, request.fullName());
        User savedUser = userRepository.save(user);

        AuditUserInfo createdBy = userLookupPort
                .findAuditInfo(savedUser.getCreatedBy()).orElse(null);
        AuditUserInfo updatedBy = userLookupPort
                .findAuditInfo(savedUser.getUpdatedBy()).orElse(null);
        return userMapper.toResponse(savedUser, createdBy, updatedBy);

    }
}