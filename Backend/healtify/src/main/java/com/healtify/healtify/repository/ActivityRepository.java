package com.healtify.healtify.repository;

import com.healtify.healtify.models.Activity;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Optional<Activity> findByUserAccount(UserAccount userAccount);
}
