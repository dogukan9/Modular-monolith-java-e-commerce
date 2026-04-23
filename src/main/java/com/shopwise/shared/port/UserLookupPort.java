package com.shopwise.shared.port;

import com.shopwise.shared.dto.AuditUserInfo;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface UserLookupPort {
    Optional<AuditUserInfo> findAuditInfo(Long userId);
    Map<Long, AuditUserInfo> findAuditInfoByIds(Set<Long> userIds);
}