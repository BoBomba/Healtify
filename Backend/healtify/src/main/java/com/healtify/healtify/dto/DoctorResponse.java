package com.healtify.healtify.dto;

import com.healtify.healtify.models.Doctor;

/** Lekarz widziany przez pacjenta. Bez maila i czegokolwiek z konta poza nazwa. */
public record DoctorResponse(
        Long doctorId,
        String doctorName,
        String specialization,
        String username
) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getDoctorId(),
                doctor.getDoctorName(),
                doctor.getSpecialization(),
                doctor.getUserAccount().getUsername()
        );
    }
}
