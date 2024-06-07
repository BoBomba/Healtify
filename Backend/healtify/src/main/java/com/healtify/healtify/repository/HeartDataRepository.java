package com.healtify.healtify.repository;

import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.models.VitalSigns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartDataRepository extends JpaRepository<VitalSigns, Long> {
    Optional<VitalSigns> findByUserAccount(UserAccount userAccount);
    List<VitalSigns> findAllByUserAccount(UserAccount userAccount);
}