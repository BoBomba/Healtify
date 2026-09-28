package com.healtify.healtify.dto;

import com.healtify.healtify.models.Role;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.security.service.RoleEnum;

import java.util.List;

/**
 * Kim jest zalogowany uzytkownik. 
 * profileCompleted mowi, czy pacjent wypelnil juz szczegolowe dane. Jesli nie, front
 * kieruje go zaraz po zalogowaniu na formularz uzupelniania zamiast na dashboard.
 */
public record CurrentUserResponse(
        Long userId,
        String username,
        String email,
        List<String> roles,
        boolean admin,
        boolean doctor,
        boolean profileCompleted
) {
    public static CurrentUserResponse from(UserAccount user, boolean profileCompleted) {
        return new CurrentUserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).sorted().toList(),
                user.hasRole(RoleEnum.ROLE_ADMIN),
                user.hasRole(RoleEnum.ROLE_DOCTOR),
                profileCompleted
        );
    }
}
