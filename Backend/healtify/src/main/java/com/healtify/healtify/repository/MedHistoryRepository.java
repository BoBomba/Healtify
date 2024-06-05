package com.healtify.healtify.repository;

import com.healtify.healtify.models.MedicalHistory;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    Optional<MedicalHistory> findByUserAccount(UserAccount userAccount);
}
