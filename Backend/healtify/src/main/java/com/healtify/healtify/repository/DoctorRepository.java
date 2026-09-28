package com.healtify.healtify.repository;

import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Optional<Doctor> findByUserAccount(UserAccount userAccount);

    boolean existsByUserAccount(UserAccount userAccount);

    /** Wyszukiwarka lekarzy dla pacjenta - po nazwisku, specjalizacji albo nazwie konta. */
    @Query("""
            select d from Doctor d
            where lower(d.doctorName) like lower(concat('%', :query, '%'))
               or lower(d.specialization) like lower(concat('%', :query, '%'))
               or lower(d.userAccount.username) like lower(concat('%', :query, '%'))
            """)
    List<Doctor> search(String query);
}
