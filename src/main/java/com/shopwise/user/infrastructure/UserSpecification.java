package com.shopwise.user.infrastructure;

import com.shopwise.user.application.dto.UserFilterRequest;
import com.shopwise.user.domain.User;
import com.shopwise.user.domain.UserRole;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class UserSpecification {
    private UserSpecification() {}


    public static Specification<User> emailContains(String email){
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) {
                return null; // null döner -> bu koşul uygulanmaz
            }
            return cb.like(
                    cb.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
            );
        };
    }

    public static Specification<User> fullNameContains(String fullName){
        return (root, query, cb) ->{
            if(fullName==null || fullName.isBlank() ){
                return null;
            }
            return cb.like(cb.lower(root.get("fullName")),
                    "%"+fullName.toLowerCase()+"%");
        };
    }

    public static Specification<User> hasRole(UserRole role) {
        return (root, query, cb) -> {
            if (role == null) return null;
            return cb.equal(root.get("role"), role);
        };
    }

    public static Specification<User> isActive(Boolean active) {
        return (root, query, cb) -> {
            if (active == null) return null;
            return cb.equal(root.get("active"), active);
        };
    }

    public static Specification<User> createdAtBetween(
            LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start == null) return cb.lessThanOrEqualTo(
                    root.get("createdAt"), end);
            if (end == null) return cb.greaterThanOrEqualTo(
                    root.get("createdAt"), start);
            return cb.between(root.get("createdAt"), start, end);

        };
    }

    public static Specification<User> build(UserFilterRequest filter) {
        return Specification
                .where(emailContains(filter.getEmail()))
                .and(fullNameContains(filter.getFullName()))
                .and(hasRole(filter.getRole()))
                .and(isActive(filter.getActive()))
                .and(createdAtBetween(
                        filter.getCreatedAtStart(),
                        filter.getCreatedAtEnd()));
        // null dönen koşullar otomatik atlanır
    }
}

