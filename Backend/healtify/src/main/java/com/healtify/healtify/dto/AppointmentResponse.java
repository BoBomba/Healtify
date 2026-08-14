package com.healtify.healtify.dto;

import com.healtify.healtify.models.Appointment;

import java.time.LocalDateTime;

/** Wizyta zwracana na front - zarowno lekarzowi, jak i pacjentowi. */
public record AppointmentResponse(
        Long appointmentId,
        LocalDateTime appointmentAt,
        String title,
        String notes,
        PatientResponse patient,
        DoctorResponse doctor
) {
    public static AppointmentResponse from(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getAppointmentId(),
                appointment.getAppointmentAt(),
                appointment.getTitle(),
                appointment.getNotes(),
                PatientResponse.from(appointment.getPatient()),
                DoctorResponse.from(appointment.getDoctor())
        );
    }
}
