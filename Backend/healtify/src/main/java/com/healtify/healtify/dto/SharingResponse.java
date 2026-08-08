package com.healtify.healtify.dto;

import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.SharingInitiator;
import com.healtify.healtify.models.SharingStatus;

import java.time.LocalDateTime;

/**
 * Powiazanie pacjent - lekarz. Ten sam ksztalt trafia do obu paneli: lekarz patrzy
 * na pole patient, pacjent na pole doctor.
 */
public record SharingResponse(
        Long sharingId,
        SharingStatus status,
        SharingInitiator initiatedBy,
        LocalDateTime requestSentDate,
        LocalDateTime requestAcceptedDate,
        PatientResponse patient,
        DoctorResponse doctor
) {
    public static SharingResponse from(DataSharing sharing) {
        return new SharingResponse(
                sharing.getSharingId(),
                sharing.getRequestStatus(),
                sharing.getInitiatedBy(),
                sharing.getRequestSentDate(),
                sharing.getRequestAcceptedDate(),
                PatientResponse.from(sharing.getUserAccount()),
                DoctorResponse.from(sharing.getDoctor())
        );
    }
}
