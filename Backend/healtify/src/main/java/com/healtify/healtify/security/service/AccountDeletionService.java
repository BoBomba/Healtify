package com.healtify.healtify.security.service;

import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.AppointmentRepository;
import com.healtify.healtify.repository.DoctorRepository;
import com.healtify.healtify.repository.JournalEntryRepository;
import com.healtify.healtify.repository.SharingRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.repository.UserProfileRepository;
import com.healtify.healtify.security.token.TokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Kasowanie konta razem ze wszystkim, co do niego nalezy.
 *
 * Wszystkie tabele z danymi uzytkownika maja klucz obcy na user_account, wiec samo
 * userRepository.delete(user) konczy sie bledem integralnosci - dzieci trzeba usunac
 * najpierw i w kolejnosci od najglebszych. Konto lekarza jest tu przypadkiem podwojnym:
 * wystepuje w bazie i jako pacjent (user_id), i jako lekarz (doctor_id), wiec obie strony
 * musza zostac posprzatane.
 * Cala operacja idzie w jednej transakcji - albo znika komplet danych, albo nic.
 */
@Service
public class AccountDeletionService {

    /**
     * Tabele powiazane z kontem, do ktorych nie ma jeszcze repozytoriow ani kodu
     * zapisujacego (sa puste, ale Hibernate zaklada dla nich klucze obce).
     * Czyscimy je zapytaniem, zeby dolozenie funkcji piszacej do ktorejs z nich
     * nie zablokowalo nagle kasowania kont.
     */
    private static final List<String> ORPHAN_ENTITIES = List.of(
            "GeneralUserData",
            "EmergencyContact",
            "ReminderSettings",
            "UserNotifications",
            "CommunityPost",
            "UserAuth"
    );

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final AppointmentRepository appointmentRepository;
    private final SharingRepository sharingRepository;
    private final DoctorRepository doctorRepository;
    private final TokenRepository tokenRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AccountDeletionService(
            UserAccountRepository userAccountRepository,
            UserProfileRepository userProfileRepository,
            JournalEntryRepository journalEntryRepository,
            AppointmentRepository appointmentRepository,
            SharingRepository sharingRepository,
            DoctorRepository doctorRepository,
            TokenRepository tokenRepository
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.appointmentRepository = appointmentRepository;
        this.sharingRepository = sharingRepository;
        this.doctorRepository = doctorRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void deleteAccount(UserAccount account) {

        // Kontroler znalazl konto poza ta transakcja, wiec encja jest odlaczona -
        // pobieramy ja jeszcze raz, zeby czyszczenie rol dzialalo na zarzadzanym obiekcie.
        UserAccount user = userAccountRepository.findById(account.getUserId()).orElse(null);
        if (user == null) {
            return;
        }

        // 1. Strona lekarza - wizyty, ktore to konto zalozylo swoim pacjentom,
        //    powiazania z pacjentami i sam profil lekarza.
        Optional<Doctor> doctor = doctorRepository.findByUserAccount(user);
        if (doctor.isPresent()) {
            appointmentRepository.deleteByDoctor(doctor.get());
            sharingRepository.deleteByDoctor(doctor.get());
            doctorRepository.delete(doctor.get());
        }

        // 2. Strona pacjenta - wizyty zalozone temu kontu przez lekarzy i powiazania z nimi.
        appointmentRepository.deleteByPatient(user);
        sharingRepository.deleteByUserAccount(user);

        // 3. Wlasne dane konta.
        journalEntryRepository.deleteByUserAccount(user);
        userProfileRepository.deleteByUserAccount(user);
        deleteOrphanRows(user);

        // 4. Tokeny JWT 
        tokenRepository.deleteByUserId(user.getUserId());

        // 5. Role z tabeli laczacej i samo konto. Role czyscimy jawnie, bo UserAccount
        //    jest wlascicielem relacji i tylko wtedy Hibernate skasuje wiersze user_roles.
        user.getRoles().clear();
        userAccountRepository.delete(user);

        // Kasowania sa mieszane (bulk + encje), wiec wymuszamy zapis w ustalonej kolejnosci
        // jeszcze wewnatrz transakcji - inaczej blad wyszedlby dopiero przy commicie.
        entityManager.flush();
    }

    private void deleteOrphanRows(UserAccount user) {
        for (String entity : ORPHAN_ENTITIES) {
            entityManager.createQuery("delete from " + entity + " e where e.userAccount = :user")
                    .setParameter("user", user)
                    .executeUpdate();
        }
    }
}
