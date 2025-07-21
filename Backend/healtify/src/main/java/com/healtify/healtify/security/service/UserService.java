package com.healtify.healtify.security.service;

import com.healtify.healtify.dto.UserDTO;
import com.healtify.healtify.models.Role;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.token.TokenRepository;
import com.healtify.healtify.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.healtify.healtify.mapper.UserMapper.mapToUserDto;

@Service
public class UserService {
    private final UserAccountRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserService(
            UserAccountRepository userRepository,
            TokenRepository tokenRepository,
            UserAccountRepository userAccountRepository
        ) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.userAccountRepository = userAccountRepository;
    }


    public Boolean UserExists(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserAccount changeUserRole(UserAccount user, String role) {
        // Sprawdź czy rola istnieje w RoleEnum
        boolean isValidRole = false;
        for (RoleEnum r : RoleEnum.values()) {
            if (r.name().equals(role)) {
                isValidRole = true;
                break;
            }
        }
        if (!isValidRole) {
            throw new RuntimeException("Nieprawidłowa rola: " + role);
        }

        if (user.getRoles().stream().anyMatch(r -> r.getName().equals(role))) {
            throw new RuntimeException("User already has this role");
        } else {
            Role newRole = new Role();
            newRole.setName(role);
            user.getRoles().add(newRole);
            return userRepository.save(user);
        }
    }


    public UserAccount createUser(UserAccount user) {
        return userRepository.save(user);
    }


    public UserAccount updateUser(String username, UserAccount user) {
        return userRepository.findByUsername(username).map(existingUser -> {
            Optional.ofNullable(user.getEmail()).ifPresent(existingUser::setEmail);
            Optional.ofNullable(user.getUsername()).ifPresent(existingUser::setUsername);
            Optional.ofNullable(user.getPassword()).ifPresent(existingUser::setPassword);
            return userRepository.save(existingUser);
        }).orElseThrow(() -> new RuntimeException("User does not exist"));
    }


    public void updateUsername(UserAccount userAccount, String newUsername) {
        if (userAccountRepository.existsByUsername(newUsername)) {
            throw new RuntimeException("Username already exists");
        } else {
            userAccount.setUsername(newUsername);
            userRepository.save(userAccount);
            System.out.println("User updated");
        }
    }

    public void updateEmail(UserAccount userAccount, String newEmail) {
        if (userAccountRepository.existsByEmail(newEmail)) {
            throw new RuntimeException("Email already exists");
        } else {
            userAccount.setEmail(newEmail);
            userRepository.save(userAccount);
            System.out.println("User updated");
        }
    }

    public void deleteById(Long userId) {
        Integer intId = userId.intValue();
        tokenRepository.deleteById(intId);
        userRepository.deleteById(userId);
        System.out.println("User deleted");
    }

    public List<UserDTO> findAllUsers() {
        List<UserAccount> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDTO findUserById(Long userId) {
        UserAccount user = userRepository.findById(userId).get();
        return mapToUserDto(user);
    }

    public UserAccount findUserDetailsById(Long userId) {
        UserAccount user = userRepository.findById(userId).get();
        return user;
    }

    public Optional<Long> getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserAccount::getUserId);
    }

    public Optional<Long> getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserAccount::getUserId);
    }

    public UserDTO findDTOByUsername(String username) {
        return userRepository.findByUsername(username).map(UserMapper::mapToUserDto).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserAccount findAccByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
    }
}