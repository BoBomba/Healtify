package com.healtify.healtify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.healtify.healtify.models.UserAccount;

import java.util.List;
import java.util.Optional;


public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    Optional<UserAccount> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    /**
     * Wyszukiwarka pacjentow dla lekarza. Szuka po nazwie konta i mailu, ale zwraca
     * tylko konta pacjentow - lekarze i admini nie moga zostac czyimis pacjentami.
     */
    @Query("""
            select u from UserAccount u
            where (lower(u.username) like lower(concat('%', :query, '%'))
                or lower(u.email) like lower(concat('%', :query, '%')))
              and not exists (
                    select r from UserAccount u2 join u2.roles r
                    where u2 = u and r.name in ('ROLE_DOCTOR', 'ROLE_ADMIN')
              )
            order by u.username asc
            """)
    List<UserAccount> searchPatients(String query);
}