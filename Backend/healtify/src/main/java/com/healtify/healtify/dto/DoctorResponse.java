package com.healtify.healtify.dto;

import com.healtify.healtify.models.Doctor;

/**
 * Lekarz widziany przez pacjenta - wizytowka zawodowa.
 * completed mowi, czy lekarz przeszedl juz przez formularz uzupelniania danych
 */
public record DoctorResponse(
        Long doctorId,
        String doctorName,
        String title,
        String specialization,
        String licenseNumber,
        String workplace,
        String workAddress,
        String phone,
        String username,
        String email,
        boolean completed
) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getDoctorId(),
                doctor.getDoctorName(),
                doctor.getTitle(),
                doctor.getSpecialization(),
                doctor.getLicenseNumber(),
                doctor.getWorkplace(),
                doctor.getWorkAddress(),
                doctor.getPhone(),
                doctor.getUserAccount().getUsername(),
                doctor.getUserAccount().getEmail(),
                doctor.isProfileCompleted()
        );
    }
}
