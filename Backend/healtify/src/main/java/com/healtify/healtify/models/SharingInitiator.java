package com.healtify.healtify.models;

/**
 * Kto wyszedl z inicjatywa. Decyduje o tym, po ktorej stronie zaproszenie jest
 * "przychodzace" (do akceptacji), a po ktorej "wyslane" (czeka na odpowiedz).
 */
public enum SharingInitiator {
    PATIENT,
    DOCTOR
}
