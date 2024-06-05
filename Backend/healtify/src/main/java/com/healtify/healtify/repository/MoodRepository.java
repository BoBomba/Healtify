package com.healtify.healtify.repository;

import com.healtify.healtify.models.MoodTracker;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MoodRepository extends JpaRepository<MoodTracker, Long> {
    Optional<MoodTracker> findByUserAccount(UserAccount userAccount);
}
