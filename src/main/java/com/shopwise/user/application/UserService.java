package com.shopwise.user.application;

import com.shopwise.shared.api.PageResponse;
import com.shopwise.shared.dto.AuditUserInfo;
import com.shopwise.shared.exception.BusinessException;
import com.shopwise.shared.port.UserLookupPort;
import com.shopwise.user.application.dto.*;
import com.shopwise.user.domain.User;
import com.shopwise.user.domain.event.UserDeactivatedEvent;
import com.shopwise.user.infrastructure.UserRepository;
import com.shopwise.user.infrastructure.UserSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserLookupPort userLookupPort;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        AuditUserInfo createdBy = userLookupPort
                .findAuditInfo(user.getCreatedBy()).orElse(null);
        AuditUserInfo updatedBy = userLookupPort
                .findAuditInfo(user.getUpdatedBy()).orElse(null);
        return userMapper.toResponse(user, createdBy, updatedBy);
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
        Set<Long> userIds = userPage.getContent().stream()
                .flatMap(u -> Stream.of(u.getCreatedBy(), u.getUpdatedBy()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, AuditUserInfo> auditUsers =
                userLookupPort.findAuditInfoByIds(userIds);

        Page<UserResponse> responsePage = userPage.map(user ->
                userMapper.toResponse(user,
                        auditUsers.get(user.getCreatedBy()),
                        auditUsers.get(user.getUpdatedBy()))
        );
        return PageResponse.of(responsePage);
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);
        user.updateFullName(request.fullName());

        AuditUserInfo createdBy = userLookupPort
                .findAuditInfo(user.getCreatedBy()).orElse(null);
        AuditUserInfo updatedBy = userLookupPort
                .findAuditInfo(user.getUpdatedBy()).orElse(null);
        return userMapper.toResponse(userRepository.save(user), createdBy, updatedBy);
    }

    public void deactivateUser(Long id) {
        User user = findUserById(id);
        user.deactivate();
        userRepository.save(user);
        eventPublisher.publishEvent(new UserDeactivatedEvent(id));
        log.info("User deactivated event published: {}", id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "USER_NOT_FOUND",
                        "Kullanıcı bulunamadı: " + id));
    }
}