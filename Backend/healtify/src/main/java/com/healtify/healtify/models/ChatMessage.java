package com.healtify.healtify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Jedna wiadomosc czatu pacjent - lekarz.
 *
 * Rozmowa jest identyfikowana przez istniejacy wiersz {@link DataSharing}, czyli przez pare
 * pacjent-lekarz. Nie ma osobnej tabeli "konwersacja", bo ta para juz jest unikalna
 * (uq_sharing_user_doctor) i ma gotowa kontrole dostepu - czat po prostu wisi pod nia.
 *
 * Tabela jest append-only: raz zapisanej wiadomosci nigdy nie nadpisujemy. Jedyne pole,
 * ktore sie zmienia po zapisie, to readAt. Edycja i kasowanie, gdyby kiedys weszly, maja
 * byc osobnymi kolumnami (edited_at / deleted_at), a nie UPDATE na tresci - historia rozmowy
 * miedzy pacjentem a lekarzem musi zostac niezmienna.
 */
@Entity
@Table(name = "chat_messages", indexes = {
        // Bez tego indeksu kazde wejscie w czat i kazde doladowanie starszych dymkow
        // to skan calej tabeli. Kolejnosc kolumn odpowiada zapytaniu paginacji:
        // WHERE sharing_id = ? AND message_id < ? ORDER BY message_id DESC.
        @Index(name = "idx_chat_messages_sharing", columnList = "sharing_id, message_id")
})
public class ChatMessage {

    /**
     * Rosnacy klucz glowny sluzy jednoczesnie za kursor paginacji - dlatego czat stronicuje
     * po message_id, a nie po dacie (dwie wiadomosci moga miec ten sam timestamp).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sharing_id", nullable = false)
    private DataSharing sharing;

    @Enumerated(EnumType.STRING)
    @Column(name = "sender", nullable = false, length = 20)
    private MessageSender sender;

    /**
     * Tresc wiadomosci. Po wlaczeniu szyfrowania (patrz MessageCryptoService) beda tu
     * lezec szyfrogramy, a nie tekst jawny - dlatego nie ma na tej kolumnie zadnego
     * wyszukiwania ani indeksu tekstowego.
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** Czas serwera. Klient nigdy nie ustawia daty wiadomosci. */
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    /** Kiedy druga strona otworzyla rozmowe. null = nieprzeczytana (liczy je kropka na liscie). */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /**
     * Wersja szyfrowania tresci. 0 = tekst jawny.
     *
     * Kolumna istnieje od pierwszego dnia, zeby wlaczenie szyfrowania nie wymagalo
     * migracji "wszystko naraz": stare wiadomosci zostaja z wersja 0, nowe dostaja
     * wyzsza, a odczyt patrzy na wersje i dobiera sposob odszyfrowania.
     */
    @Column(name = "encryption_version", nullable = false)
    private int encryptionVersion;

    // Getters and setters

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public DataSharing getSharing() {
        return sharing;
    }

    public void setSharing(DataSharing sharing) {
        this.sharing = sharing;
    }

    public MessageSender getSender() {
        return sender;
    }

    public void setSender(MessageSender sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public int getEncryptionVersion() {
        return encryptionVersion;
    }

    public void setEncryptionVersion(int encryptionVersion) {
        this.encryptionVersion = encryptionVersion;
    }

    // constructors

    public ChatMessage() {
    }

    public ChatMessage(DataSharing sharing, MessageSender sender, String content, int encryptionVersion) {
        this.sharing = sharing;
        this.sender = sender;
        this.content = content;
        this.encryptionVersion = encryptionVersion;
        this.sentAt = LocalDateTime.now();
    }
}
