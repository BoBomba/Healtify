package com.healtify.healtify.models;

/**
 * Kto napisal wiadomosc. Trzymamy strone rozmowy, a nie user_id, bo obie strony
 * wynikaja juz z wiersza data_sharing - dublowanie ich w kazdej wiadomosci moglo
 * by sie rozjechac z powiazaniem.
 */
public enum MessageSender {
    PATIENT,
    DOCTOR
}
