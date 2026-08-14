package com.healtify.healtify.models;

/** Stan powiazania pacjent - lekarz. */
public enum SharingStatus {
    /** Zaproszenie wyslane, druga strona jeszcze nie odpowiedziala. */
    PENDING,
    /** Obie strony zgodne - lekarz widzi pacjenta i moze mu zakladac wizyty. */
    ACCEPTED,
    /** Odrzucone. Zostaje w bazie, zeby nie dalo sie zasypac kogos zaproszeniami w kolko. */
    REJECTED
}
