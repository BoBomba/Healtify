package com.healtify.healtify.repository;

import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.SharingStatus;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SharingRepository extends JpaRepository<DataSharing, Long> {

    // --- strona pacjenta ---

    List<DataSharing> findByUserAccountOrderByRequestSentDateDesc(UserAccount userAccount);

    List<DataSharing> findByUserAccountAndRequestStatusOrderByRequestSentDateDesc(
            UserAccount userAccount, SharingStatus requestStatus);

    // --- strona lekarza ---

    List<DataSharing> findByDoctorOrderByRequestSentDateDesc(Doctor doctor);

    List<DataSharing> findByDoctorAndRequestStatusOrderByRequestSentDateDesc(
            Doctor doctor, SharingStatus requestStatus);

    // --- wspolne ---

    /** Jedno powiazanie na pare pacjent-lekarz (pilnuje tego takze unique constraint na tabeli). */
    Optional<DataSharing> findByUserAccountAndDoctor(UserAccount userAccount, Doctor doctor);

    boolean existsByUserAccountAndDoctorAndRequestStatus(
            UserAccount userAccount, Doctor doctor, SharingStatus requestStatus);
}
