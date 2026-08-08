package com.healtify.healtify.repository;

import com.healtify.healtify.models.Appointment;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /** Nadchodzace wizyty lekarza - dashboard i lista "wszystkie przyszle wizyty". */
    List<Appointment> findByDoctorAndAppointmentAtGreaterThanEqualOrderByAppointmentAtAsc(
            Doctor doctor, LocalDateTime from);

    /** Wszystkie wizyty lekarza (kalendarz pokazuje takze miesiace wstecz). */
    List<Appointment> findByDoctorOrderByAppointmentAtAsc(Doctor doctor);

    /** Wizyty pacjenta - wylacznie do odczytu po stronie pacjenta. */
    List<Appointment> findByPatientOrderByAppointmentAtAsc(UserAccount patient);

    long countByDoctor(Doctor doctor);
}
