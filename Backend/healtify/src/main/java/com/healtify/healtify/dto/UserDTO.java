package com.healtify.healtify.dto;

import com.healtify.healtify.models.UserAccount;

public class UserDTO {
    private Long id;
    private String username;
    private String email;


    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;

    }

    public Long getId() {
        return id;
    }

    public UserDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserDTO setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserDTO setUsername(String username) {
        this.username = username;
        return this;
    }

     public static class Builder {
        private Long id;
        private String username;
        private String email;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, username, email);
        }
     }

        public static UserDTO mapToUserDto(UserAccount user) {
            return new UserDTO.Builder()
                    .setId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .build();
    }

    public static UserAccount mapFromUserDto(UserDTO userDto) {
        return new UserAccount.Builder()
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .build();
    }
}
