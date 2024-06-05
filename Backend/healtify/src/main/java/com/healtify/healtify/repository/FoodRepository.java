package com.healtify.healtify.repository;

import com.healtify.healtify.models.NutritionLog;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodRepository extends JpaRepository<NutritionLog, Long> {
    Optional<NutritionLog> findByUserAccount(UserAccount userAccount);
}
