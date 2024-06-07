package com.healtify.healtify.repository;

import com.healtify.healtify.models.Medication;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicationsRepository extends JpaRepository<Medication, Long> {
    Optional<Medication> findByUserAccount(UserAccount userAccount);
}
