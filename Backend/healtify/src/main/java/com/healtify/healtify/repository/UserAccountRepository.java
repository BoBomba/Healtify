package com.healtify.healtify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.healtify.healtify.models.UserAccount;

import java.util.Optional;


public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUsername(String username);
}