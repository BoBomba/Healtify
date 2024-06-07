package com.healtify.healtify.repository;

import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SharingRepository extends JpaRepository<DataSharing, Long> {
    Optional<DataSharing> findByUserAccount(UserAccount userAccount);
}
