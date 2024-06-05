package com.healtify.healtify.repository;

import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByUserAccount(UserAccount userAccount);
}