package com.healtify.healtify.repository;

import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SettingsRepository extends JpaRepository<UserAccount, Long>
{
//    Optional<UserAccount> findByUserAccount(UserAccount userAccount);
}
