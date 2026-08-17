package com.healtify.healtify.service;

import com.healtify.healtify.dto.ChatConversationResponse;
import com.healtify.healtify.dto.ChatMessageResponse;
import com.healtify.healtify.dto.ChatPageResponse;
import com.healtify.healtify.models.ChatMessage;
import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.MessageSender;
import com.healtify.healtify.models.SharingStatus;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.ChatMessageRepository;
import com.healtify.healtify.repository.DoctorRepository;
import com.healtify.healtify.repository.SharingRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.service.MessageCryptoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Czat pacjent - lekarz. Cala logika siedzi tu, bo maja z niej korzystac dwa wejscia:
 * REST ({@code /api/chat}) i WebSocket STOMP ({@code /app/chat.send}). Kontrolery sa tylko
 * cienka warstwa nad tym serwisem, wiec regul dostepu nie da sie obejsc, wchodzac druga droga.
 *
 * Zasady:
 * - rozmowa to wiersz data_sharing, a nie osobny byt - jedna para pacjent-lekarz = jeden watek;
 * - kazda operacja przechodzi przez {@link #access}, ktory wymaga statusu ACCEPTED, wiec po
 *   cofnieciu zgody czat znika razem z reszta dostepu do danych pacjenta (wiersze zostaja w bazie
 *   i wracaja, gdy powiazanie zostanie odnowione);
 * - nadawca wynika z tokenu, nigdy z tresci zadania.
 */
@Service
public class ChatService {

    /**
     * Ile dymkow leci w jednej porcji. Celowo malo - chodzi o to, zeby przy wejsciu
     * nie wysylac calej historii, a przycisk "starsze" dawal sie przetestowac na kilku
     * wiadomosciach. Docelowo 20-30.
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /** Gorna granica dla parametru ?limit - zeby klient nie wyciagnal calej rozmowy jednym strzalem. */
    private static final int MAX_PAGE_SIZE = 50;

    private static final int MAX_CONTENT_LENGTH = 2000;

    /** Zabezpieczenie przed zasypaniem bazy z jednej rozmowy (jak MAX_APPOINTMENTS_PER_DOCTOR). */
    private static final long MAX_MESSAGES_PER_CONVERSATION = 20000;

    /**
     * Kolejka uzytkownika, na ktora leca nowe wiadomosci. Swiadomie /user/queue/... zamiast
     * /topic/chat/{sharingId}: temat publiczny musialby miec wlasna autoryzacje przy SUBSCRIBE
     * (kazdy zalogowany moglby sie zapisac na cudza rozmowe), a kolejka uzytkownika jest
     * z definicji prywatna - Spring rozwiazuje ja po nazwie konta z tokenu.
     */
    public static final String USER_QUEUE = "/queue/chat";

    private final ChatMessageRepository chatMessageRepository;
    private final SharingRepository sharingRepository;
    private final UserAccountRepository userAccountRepository;
    private final DoctorRepository doctorRepository;
    private final MessageCryptoService messageCrypto;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(
            ChatMessageRepository chatMessageRepository,
            SharingRepository sharingRepository,
            UserAccountRepository userAccountRepository,
            DoctorRepository doctorRepository,
            MessageCryptoService messageCrypto,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.sharingRepository = sharingRepository;
        this.userAccountRepository = userAccountRepository;
        this.doctorRepository = doctorRepository;
        this.messageCrypto = messageCrypto;
        this.messagingTemplate = messagingTemplate;
    }

    /** Kim jest zalogowany uzytkownik w rozmowie. Widoczne tylko wewnatrz serwisu. */
    private record ChatAccess(DataSharing sharing, MessageSender role, UserAccount user) {
    }

    // --- dostep ---

    /**
     * Wpuszcza do rozmowy tylko jej strone i tylko przy aktywnym powiazaniu.
     *
     * Kazde odbicie to 404, nie 403 - inaczej odpowiedz potwierdzalaby, ze rozmowa
     * o takim id istnieje, a to juz jest informacja poufna o cudzej relacji pacjent-lekarz.
     *
     * Metoda jest prywatna i CELOWO bez @Transactional: sprawdzenie dostepu ma
     * dziac sie w tej samej transakcji, co operacja, ktora obsluguje. Osobna transakcja
     * konczylaby sie odlaczeniem encji, a pola (doctor, userAccount) sypalyby 
     * potem bledem "could not initialize proxy - no Session". Przez HTTP maskowal to
     * open-in-view, ale wiadomosci z WebSocketa nie ida przez filtr MVC i nie maja sesji.
     */
    private ChatAccess resolveAccess(String username, Long sharingId) {
        UserAccount user = requireUser(username);
        DataSharing sharing = sharingRepository.findById(sharingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiej rozmowy"));

        MessageSender role = roleIn(sharing, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiej rozmowy"));

        if (sharing.getRequestStatus() != SharingStatus.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ta rozmowa nie jest juz dostepna");
        }
        return new ChatAccess(sharing, role, user);
    }

    private Optional<MessageSender> roleIn(DataSharing sharing, UserAccount user) {
        if (sharing.getUserAccount().getUserId().equals(user.getUserId())) {
            return Optional.of(MessageSender.PATIENT);
        }
        if (sharing.getDoctor().getUserAccount().getUserId().equals(user.getUserId())) {
            return Optional.of(MessageSender.DOCTOR);
        }
        return Optional.empty();
    }

    // --- historia ---

    /**
     * Pobieranie historii. Bez {@code before} najnowsze wiadomosci (wejscie w czat),
     * z {@code before} - te starsze od podanego id (przycisk rozwiniecia rozmowy).
     */
    @Transactional(readOnly = true)
    public ChatPageResponse loadHistory(String username, Long sharingId, Long before, Integer limit) {
        ChatAccess access = resolveAccess(username, sharingId);
        int size = pageSize(limit);

        // Pobieramy o jedna wiecej, niz oddamy - nadmiarowy wiersz to
        // odpowiedz na pytanie "czy jest jeszcze cos starszego", bez osobnego COUNT-a.
        var pageable = PageRequest.of(0, size + 1);
        List<ChatMessage> found = before == null
                ? chatMessageRepository.findBySharingOrderByMessageIdDesc(access.sharing(), pageable)
                : chatMessageRepository.findBySharingAndMessageIdLessThanOrderByMessageIdDesc(
                        access.sharing(), before, pageable);

        boolean hasMore = found.size() > size;
        List<ChatMessage> page = hasMore ? found.subList(0, size) : found;

        // Baza oddaje od najnowszej, front rysuje od najstarszej,
        // zeby kolejnosc dymkow nie zalezala od tego, ktory klient odbiera.
        List<ChatMessageResponse> messages = new ArrayList<>(page.stream().map(this::toResponse).toList());
        Collections.reverse(messages);

        return new ChatPageResponse(messages, hasMore);
    }

    private int pageSize(Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(limit, MAX_PAGE_SIZE);
    }

    // --- wysylanie ---

    /**
     * Zapis wiadomosci i push do obu stron.
     *
     * Nadawca tez dostaje kopie po WebSockecie - dzieki temu wiadomosc
     * pojawia sie we wszystkich otwartych kartach tego samego konta. 
     * Front odsiewa duplikaty po messageId.
     */
    @Transactional
    public ChatMessageResponse send(String username, Long sharingId, String rawContent) {
        ChatAccess access = resolveAccess(username, sharingId);

        String content = rawContent == null ? "" : rawContent.trim();
        if (content.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wiadomosc nie moze byc pusta");
        }
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Wiadomosc moze miec najwyzej " + MAX_CONTENT_LENGTH + " znakow");
        }
        if (chatMessageRepository.countBySharing(access.sharing()) >= MAX_MESSAGES_PER_CONVERSATION) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Osiagnieto limit wiadomosci");
        }

        ChatMessage saved = chatMessageRepository.save(new ChatMessage(
                access.sharing(),
                access.role(),
                messageCrypto.encrypt(content),
                MessageCryptoService.CURRENT_VERSION
        ));

        ChatMessageResponse response = ChatMessageResponse.of(saved, content);
        for (String participant : participants(access.sharing())) {
            messagingTemplate.convertAndSendToUser(participant, USER_QUEUE, response);
        }
        return response;
    }

    // Nazwy kont obu stron rozmowy - po nich WB rozpoznaje, do kogo wyslac push.
    private List<String> participants(DataSharing sharing) {
        return List.of(
                sharing.getUserAccount().getUsername(),
                sharing.getDoctor().getUserAccount().getUsername()
        );
    }

    // --- odczyt ---

    // Otwarcie rozmowy kasuje licznik unread po danej stronie.
    @Transactional
    public int markRead(String username, Long sharingId) {
        ChatAccess access = resolveAccess(username, sharingId);
        return chatMessageRepository.markConversationRead(
                access.sharing(), access.role(), LocalDateTime.now());
    }

    // --- lista rozmow ---

    /**
     * Rozmowy zalogowanego uzytkownika - z obu stron naraz, bo jedno konto moze wystepowac
     * i jako pacjent, i jako lekarz. Front dopasowuje je do swoich list po doctorId / patientId
     * stad bierze zarowno sharingId do linku, jak i liczbe nieprzeczytanych.
     */
    @Transactional(readOnly = true)
    public List<ChatConversationResponse> conversations(String username) {
        UserAccount user = requireUser(username);
        List<ChatConversationResponse> conversations = new ArrayList<>();

        for (DataSharing sharing : sharingRepository
                .findByUserAccountAndRequestStatusOrderByRequestSentDateDesc(user, SharingStatus.ACCEPTED)) {
            conversations.add(toConversation(sharing, MessageSender.PATIENT));
        }

        Optional<Doctor> doctor = doctorRepository.findByUserAccount(user);
        if (doctor.isPresent()) {
            for (DataSharing sharing : sharingRepository
                    .findByDoctorAndRequestStatusOrderByRequestSentDateDesc(doctor.get(), SharingStatus.ACCEPTED)) {
                conversations.add(toConversation(sharing, MessageSender.DOCTOR));
            }
        }
        return conversations;
    }

    // Ta sama rozmowa opisana dla jednej ze stron - uzywa tego tez naglowek czatu.
    @Transactional(readOnly = true)
    public ChatConversationResponse describe(String username, Long sharingId) {
        ChatAccess access = resolveAccess(username, sharingId);
        return toConversation(access.sharing(), access.role());
    }

    private ChatConversationResponse toConversation(DataSharing sharing, MessageSender myRole) {
        Doctor doctor = sharing.getDoctor();
        UserAccount patient = sharing.getUserAccount();

        // Nazwa lekarza jest opcjonalna - wtedy zostaje nazwa konta.
        String doctorName = doctor.getDoctorName() == null || doctor.getDoctorName().isBlank()
                ? doctor.getUserAccount().getUsername()
                : doctor.getDoctorName();

        return new ChatConversationResponse(
                sharing.getSharingId(),
                myRole,
                doctor.getDoctorId(),
                patient.getUserId(),
                myRole == MessageSender.PATIENT ? doctorName : patient.getUsername(),
                chatMessageRepository.countBySharingAndSenderNotAndReadAtIsNull(sharing, myRole),
                chatMessageRepository.findFirstBySharingOrderByMessageIdDesc(sharing)
                        .map(ChatMessage::getSentAt)
                        .orElse(null)
        );
    }

    // --- helpery ---

    private UserAccount requireUser(String username) {
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji");
        }
        return userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji"));
    }

    private ChatMessageResponse toResponse(ChatMessage message) {
        return ChatMessageResponse.of(
                message,
                messageCrypto.decrypt(message.getContent(), message.getEncryptionVersion())
        );
    }
}
