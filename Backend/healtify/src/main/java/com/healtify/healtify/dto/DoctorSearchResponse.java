package com.healtify.healtify.dto;

import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.SharingInitiator;
import com.healtify.healtify.models.SharingStatus;

/** Odpowiednik PatientSearchResponse po stronie pacjenta szukajacego lekarza. */
public record DoctorSearchResponse(
        Long doctorId,
        String doctorName,
        String specialization,
        String username,
        SharingStatus status,
        SharingInitiator initiatedBy
) {
    public static DoctorSearchResponse from(Doctor doctor, SharingStatus status, SharingInitiator initiatedBy) {
        return new DoctorSearchResponse(
                doctor.getDoctorId(),
                doctor.getDoctorName(),
                doctor.getSpecialization(),
                doctor.getUserAccount().getUsername(),
                status,
                initiatedBy
        );
    }
}
