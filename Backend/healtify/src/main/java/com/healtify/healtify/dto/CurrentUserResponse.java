package com.healtify.healtify.dto;

import com.healtify.healtify.models.Role;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.security.service.RoleEnum;

import java.util.List;

/**
 * Kim jest zalogowany uzytkownik. Front pyta o to raz po zalogowaniu i na tej podstawie
 * decyduje, czy pokazac panel pacjenta czy lekarza - stad gotowe flagi zamiast
 * przepisywania nazw rol w kilku miejscach w JS.
 */
public record CurrentUserResponse(
        Long userId,
        String username,
        String email,
        List<String> roles,
        boolean admin,
        boolean doctor
) {
    public static CurrentUserResponse from(UserAccount user) {
        return new CurrentUserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream().map(Role::getName).sorted().toList(),
                user.hasRole(RoleEnum.ROLE_ADMIN),
                user.hasRole(RoleEnum.ROLE_DOCTOR)
        );
    }
}
