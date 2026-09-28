package com.healtify.healtify.security.service;

import org.springframework.stereotype.Service;

/**
 * Jedyne miejsce, przez ktore przechodzi tresc wiadomosci czatu w droge do bazy i z powrotem.
 *
 * TERAZ: przelotka. encrypt() oddaje tekst 1:1 i stempluje wiadomosc wersja
 * {@link #CURRENT_VERSION} = 0 (tekst jawny), decrypt() dla wersji 0 tez oddaje 1:1.
 * Dzieki temu kontroler i front juz dzis wolaja docelowe API i wlaczenie szyfrowania
 * nie ruszy niczego poza tym plikiem.
 *
 * TODO (szyfrowanie czatu): zdecydowac miedzy dwoma wariantami i zaimplementowac ponizej.
 *
 * 1. At-rest (szyfrowanie po stronie serwera) - PROSTSZY, dobry pierwszy krok:
 *    - AES-256-GCM, klucz z zewnatrz aplikacji (zmienna srodowiskowa / vault), NIGDY w repo
 *      ani w application.yml obok hasla do bazy;
 *    - encrypt(): losowe 12-bajtowe IV na kazda wiadomosc, zapis jako Base64(IV || ciphertext || tag),
 *      CURRENT_VERSION = 1;
 *    - decrypt(): rozbior po wersji - 0 zwraca tekst jawny (stare wiersze zostaja jak byly),
 *      1 odszyfrowuje AES-GCM. Dzieki wersji nie trzeba migrowac calej tabeli naraz;
 *    - rotacja klucza: nowa wersja (2, 3...) zamiast podmiany klucza w miejscu;
 *    - co to daje: wyciek dumpa bazy nie ujawnia rozmow. Czego NIE daje: serwer aplikacji
 *      widzi tresc, wiec chroni przed kradzieza bazy, nie przed dostepem do serwera.
 *
 * 2. E2E (klucze u klientow) - MOCNIEJSZY, duzo wiecej roboty:
 *    - kazde konto ma pare kluczy, klucz prywatny nie opuszcza urzadzenia, serwer trzyma
 *      wylacznie szyfrogramy i klucze publiczne;
 *    - ta klasa staje sie wtedy pusta (serwer nic nie szyfruje - dostaje gotowy szyfrogram),
 *      a cala logika ladzie we froncie (WebCrypto);
 *    - koszt: zarzadzanie kluczami, utrata historii po zmianie urzadzenia/wyczyszczeniu
 *      przegladarki, brak wyszukiwania po tresci, brak podgladu dla administracji.
 *
 * Do czasu wyboru pole encryption_version w bazie jest juz na miejscu i nic nie blokuje.
 */
@Service
public class MessageCryptoService {

    /** Wersja stemplowana na nowych wiadomosciach. 0 = tekst jawny. */
    public static final int CURRENT_VERSION = 0;

    /** Wolane tuz przed zapisem wiadomosci. */
    public String encrypt(String plainText) {
        // TODO: patrz opis klasy - tu wchodzi AES-GCM i CURRENT_VERSION rosnie do 1.
        return plainText;
    }

    /** Wolane przy kazdym odczycie; wersja mowi, czym wiersz byl szyfrowany przy zapisie. */
    public String decrypt(String storedContent, int encryptionVersion) {
        if (encryptionVersion == 0) {
            return storedContent;
        }
        // TODO: obsluga kolejnych wersji. Nieznana wersja nie moze wywalic calej rozmowy -
        // lepiej pokazac zastepczy tekst przy jednym dymku niz zwrocic 500 na cala historie.
        return "[nie udalo sie odczytac wiadomosci]";
    }
}
