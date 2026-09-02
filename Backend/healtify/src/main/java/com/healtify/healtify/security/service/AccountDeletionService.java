package com.healtify.healtify.security.service;

import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.AppointmentRepository;
import com.healtify.healtify.repository.ChatMessageRepository;
import com.healtify.healtify.repository.DoctorRepository;
import com.healtify.healtify.repository.JournalEntryRepository;
import com.healtify.healtify.repository.JournalEntryShareRepository;
import com.healtify.healtify.repository.RoleRepository;
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
 * Kasowanie konta razem ze wszystkim, co do niego nalezy - i odebranie roli lekarza
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
     * Nazwy sa stringami - kasujac encje trzeba usunac wpis tez tutaj, kompilator tego nie zlapie.
     */
    private static final List<String> ORPHAN_ENTITIES = List.of(
            "EmergencyContact",
            "ReminderSettings",
            "UserNotifications",
            "CommunityPost",
            "UserAuth"
    );

    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final JournalEntryRepository journalEntryRepository;
    private final JournalEntryShareRepository journalEntryShareRepository;
    private final AppointmentRepository appointmentRepository;
    private final SharingRepository sharingRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final DoctorRepository doctorRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public AccountDeletionService(
            UserAccountRepository userAccountRepository,
            UserProfileRepository userProfileRepository,
            JournalEntryRepository journalEntryRepository,
            JournalEntryShareRepository journalEntryShareRepository,
            AppointmentRepository appointmentRepository,
            SharingRepository sharingRepository,
            ChatMessageRepository chatMessageRepository,
            DoctorRepository doctorRepository,
            RoleRepository roleRepository,
            TokenRepository tokenRepository
    ) {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.journalEntryRepository = journalEntryRepository;
        this.journalEntryShareRepository = journalEntryShareRepository;
        this.appointmentRepository = appointmentRepository;
        this.sharingRepository = sharingRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.doctorRepository = doctorRepository;
        this.roleRepository = roleRepository;
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
        deleteDoctorSide(user);

        // 2. Strona pacjenta - wizyty zalozone temu kontu przez lekarzy i powiazania z nimi.
        appointmentRepository.deleteByPatient(user);
        deleteChatMessages(sharingRepository.findByUserAccountOrderByRequestSentDateDesc(user));
        sharingRepository.deleteByUserAccount(user);

        // 3. Wlasne dane konta.
        journalEntryShareRepository.deleteByJournalEntry_UserAccount(user);
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

    /**
     * Odebranie roli lekarza. Konto zostaje i dziala dalej jako pacjent - znika tylko
     * strona lekarska, dokladnie ta sama, ktora czysci kasowanie konta.
     */
    @Transactional
    public void revokeDoctor(UserAccount account) {
        UserAccount user = userAccountRepository.findById(account.getUserId()).orElse(null);
        if (user == null) {
            return;
        }

        deleteDoctorSide(user);

        user.getRoles().removeIf(role -> RoleEnum.ROLE_DOCTOR.name().equals(role.getName()));

        // Konto bez zadnej roli nie przeszloby autoryzacji - zostaje zwyklym pacjentem.
        if (user.getRoles().isEmpty()) {
            roleRepository.findByName(RoleEnum.ROLE_USER.name()).ifPresent(user.getRoles()::add);
        }

        userAccountRepository.save(user);
        entityManager.flush();
    }

    /**
     * Wszystko, co wisi na profilu lekarza: wizyty zalozone pacjentom, udostepnione mu
     * wpisy, czat, powiazania i sam profil. Kolejnosc wynika z kluczy obcych - wpisy przed
     * profilem, bo journal_entry_shares wskazuje na doctor_id, a czat przed powiazaniami.
     * Konto, ktore nie jest lekarzem, przechodzi tedy bez zmian.
     */
    private void deleteDoctorSide(UserAccount user) {
        Optional<Doctor> doctor = doctorRepository.findByUserAccount(user);
        if (doctor.isEmpty()) {
            return;
        }

        appointmentRepository.deleteByDoctor(doctor.get());
        journalEntryShareRepository.deleteByDoctor(doctor.get());
        deleteChatMessages(sharingRepository.findByDoctorOrderByRequestSentDateDesc(doctor.get()));
        sharingRepository.deleteByDoctor(doctor.get());
        doctorRepository.delete(doctor.get());
    }

    /**
     * Wiadomosci czatu wisza na data_sharing, wiec musza zniknac przed powiazaniami -
     * inaczej kasowanie konta wywala sie na kluczu obcym chat_messages.sharing_id.
     * Pusta lista jest odsiewana, bo "delete ... where sharing in ()" to nieprawidlowy SQL.
     */
    private void deleteChatMessages(List<DataSharing> sharings) {
        if (!sharings.isEmpty()) {
            chatMessageRepository.deleteBySharingIn(sharings);
        }
    }

    private void deleteOrphanRows(UserAccount user) {
        for (String entity : ORPHAN_ENTITIES) {
            entityManager.createQuery("delete from " + entity + " e where e.userAccount = :user")
                    .setParameter("user", user)
                    .executeUpdate();
        }
    }
}
