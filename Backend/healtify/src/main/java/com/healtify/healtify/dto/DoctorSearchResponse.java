package com.healtify.healtify.dto;

import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.SharingInitiator;
import com.healtify.healtify.models.SharingStatus;

/** Odpowiednik PatientSearchResponse po stronie pacjenta szukajacego lekarza. */
public record DoctorSearchResponse(
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
        SharingStatus status,
        SharingInitiator initiatedBy
) {
    public static DoctorSearchResponse from(Doctor doctor, SharingStatus status, SharingInitiator initiatedBy) {
        return new DoctorSearchResponse(
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
                status,
                initiatedBy
        );
    }
}
