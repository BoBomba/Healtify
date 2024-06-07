package com.healtify.healtify.repository;

import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.models.SymptomTracker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SymptomTrackerRepository extends JpaRepository<SymptomTracker, Long> {
    Optional<SymptomTracker> findByUserAccount(UserAccount userAccount);
}
