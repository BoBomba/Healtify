package com.healtify.healtify.repository;

import com.healtify.healtify.models.SleepRecord;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SleepRepository extends JpaRepository<SleepRecord, Long> {
    Optional<SleepRecord> findByUserAccount(UserAccount userAccount);
}
