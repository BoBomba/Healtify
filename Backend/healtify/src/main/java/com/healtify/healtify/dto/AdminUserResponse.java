package com.healtify.healtify.dto;

import com.healtify.healtify.models.Role;
import com.healtify.healtify.models.UserAccount;

import java.util.List;

/** Uzytkownik na liscie w panelu admina - z rolami, zeby bylo widac komu nadano lekarza. */
public record AdminUserResponse(
        Long userId,
        String username,
        String email,
        List<String> roles
) {
    public static AdminUserResponse from(UserAccount user) {
        return new AdminUserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).sorted().toList()
        );
    }
}
