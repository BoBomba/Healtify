package com.healtify.healtify.dto;

import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.models.UserProfile;

/**
 * Pacjent na liscie "Twoi pacjenci" - wizytowka: kontakt plus kilka pol z profilu,
 * ktore lekarz ma pod reka bez wchodzenia w szczegoly.
 *
 * SWIADOMIE osobny typ od PatientResponse. Tamten leci takze w SharingResponse, czyli
 * rowniez przy zaproszeniach ze statusem PENDING 
 * Powstaje wylacznie w DoctorController#getMyPatients, ktory filtruje po ACCEPTED.
 */
public record DoctorPatientResponse(
        Long userId,
        String username,
        String email,
        String fullName,
        Integer age,
        String gender,
        String phone
) {
    public static DoctorPatientResponse from(UserAccount user, UserProfile profile) {
        return new DoctorPatientResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                profile == null ? null : profile.getFullName(),
                profile == null ? null : profile.getAge(),
                profile == null ? null : profile.getGender(),
                profile == null ? null : profile.getPhone()
        );
    }
}
