package com.shopwise.user.application;

import com.shopwise.user.domain.event.UserDeactivatedEvent;
import com.shopwise.user.domain.event.UserLoggedInEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final UserCacheService userCacheService;

    @EventListener
    public void onUserLoggedIn(UserLoggedInEvent event) {
        log.info("UserLoggedInEvent received: {}", event.userId());
        userCacheService.addToWhitelist(event.userId());
    }

    @EventListener
    public void onUserDeactivated(UserDeactivatedEvent event) {
        log.info("UserDeactivatedEvent received: {}", event.userId());
        userCacheService.removeFromWhitelist(event.userId());
    }
}