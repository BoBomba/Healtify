package com.healtify.healtify.repository;

import com.healtify.healtify.models.CalendarEvent;
import com.healtify.healtify.models.Medication;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalendarRepository extends JpaRepository<CalendarEvent, Long> {
    Optional<CalendarEvent> findByUserAccount(UserAccount userAccount);
}
