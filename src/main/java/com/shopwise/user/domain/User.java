package com.shopwise.user.domain;


import com.shopwise.shared.domain.BaseEntity;
import com.shopwise.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//Dışarıdan new User() diyemezsin. Nesneyi sadece User.create(...) ile oluşturursun -> geçersiz state imkansız.
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean active = true;

    public static User create(String email, String password, String fullName) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.fullName = fullName;
        user.role = UserRole.CUSTOMER;
        return user;
    }

    public void deactivate() {
        if (!this.active) {
            throw new BusinessException("USER_ALREADY_INACTIVE", "User is already inactive");
        }
        this.active = false;
    }

    public void changePassword(String newPassword) {
        if (newPassword == null || newPassword.length() < 6) {
            throw new BusinessException("INVALID_PASSWORD", "Password must be at least 6 characters");
        }
        this.password = newPassword;
    }

    public void updateFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new BusinessException("INVALID_FULL_NAME", "Ad soyad boş olamaz");
        }
        this.fullName = fullName;
    }

}