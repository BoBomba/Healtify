package com.healtify.healtify.repository;

import com.healtify.healtify.models.ChatMessage;
import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.MessageSender;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // --- paginacja kursorowa (keyset) ---
    //
    // Czat pobiera najnowsza porcje wiadomosci, a starsze doladowuje szewronem w gore.
    // Oba zapytania ida od najnowszej w dol i sa stronicowane po message_id, a nie przez
    // OFFSET: przy OFFSET dopisanie nowej wiadomosci w trakcie przewijania przesuwa cale
    // okno i uzytkownik widzi duplikaty albo gubi dymki. Kursor jest na to odporny
    // i trafia prosto w indeks (sharing_id, message_id).

    /** Pierwsze wejscie w czat - najnowsze wiadomosci. */
    List<ChatMessage> findBySharingOrderByMessageIdDesc(DataSharing sharing, Pageable pageable);

    /** Doladowanie starszej porcji - wszystko przed juz pokazanym najstarszym dymkiem. */
    List<ChatMessage> findBySharingAndMessageIdLessThanOrderByMessageIdDesc(
            DataSharing sharing, Long messageId, Pageable pageable);

    // --- lista rozmow ---

    /** Ostatnia wiadomosc rozmowy - do sortowania i podpisu na liscie. */
    Optional<ChatMessage> findFirstBySharingOrderByMessageIdDesc(DataSharing sharing);

    /** Nieprzeczytane, czyli wiadomosci od drugiej strony bez daty odczytu. */
    int countBySharingAndSenderNotAndReadAtIsNull(DataSharing sharing, MessageSender sender);

    /** Dlugosc rozmowy - pilnuje limitu wiadomosci na watek. */
    long countBySharing(DataSharing sharing);

    /**
     * Oznaczenie rozmowy jako przeczytanej. Bulk update zamiast petli po encjach -
     * przy dlugiej historii nie ma sensu wciagac wszystkiego do pamieci.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ChatMessage m set m.readAt = :now "
            + "where m.sharing = :sharing and m.sender <> :reader and m.readAt is null")
    int markConversationRead(
            @Param("sharing") DataSharing sharing,
            @Param("reader") MessageSender reader,
            @Param("now") LocalDateTime now);

    // --- kasowanie konta (patrz AccountDeletionService) ---

    /** Wiadomosci wisza na data_sharing, wiec musza zniknac przed powiazaniami. */
    void deleteBySharingIn(List<DataSharing> sharings);
}
