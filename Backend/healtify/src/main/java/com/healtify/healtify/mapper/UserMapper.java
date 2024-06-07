package com.healtify.healtify.mapper;

import com.healtify.healtify.dto.UserDTO;
import com.healtify.healtify.models.UserAccount;

public class UserMapper {
    public static UserAccount mapFromUserDto(UserDTO userDto) {
        return new UserAccount.Builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .build();
    }

    public static UserDTO mapToUserDto(UserAccount user) {
        return new UserDTO.Builder()
                .setId(user.getUserId())
                .setUsername(user.getUsername())
                .setEmail(user.getEmail())
                .build();
    }
}