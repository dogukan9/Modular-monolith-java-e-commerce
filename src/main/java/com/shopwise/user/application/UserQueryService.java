package com.shopwise.user.application;

import com.shopwise.shared.dto.AuditUserInfo;
import com.shopwise.shared.port.UserLookupPort;
import com.shopwise.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService implements UserLookupPort {

    private final UserRepository userRepository;

    // Dışarıya açık tek method sadece gerekli bilgileri döndür
    @Override
     public Optional<AuditUserInfo> findAuditInfo(Long userId) {
        if (userId == null || userId == 0L) return Optional.empty();
        return userRepository.findById(userId)
                .map(user -> new AuditUserInfo(user.getId(), user.getFullName()));
    }

    @Override
     public Map<Long, AuditUserInfo> findAuditInfoByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Map.of();

        return userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(
                        user -> user.getId(),
                        user -> new AuditUserInfo(user.getId(), user.getFullName())
                ));
    }
}