package com.healtify.healtify;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import com.healtify.healtify.models.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
}