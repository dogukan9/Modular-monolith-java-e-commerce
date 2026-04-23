package com.shopwise.shared.api;

import com.shopwise.shared.exception.BusinessException;
import com.shopwise.user.application.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {}

    // Şu an login olan kullanıcının id'sini getir
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("UNAUTHORIZED", "Giriş yapılmamış");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUserId();
    }

    // Şu an login olan kullanıcının email'ini getir
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("UNAUTHORIZED", "Giriş yapılmamış");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    //  Şu an login olan kullanıcının role'ünü getir
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder
                .getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("UNAUTHORIZED", "Giriş yapılmamış");
        }

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getRole();
    }
}