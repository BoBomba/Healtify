package com.healtify.healtify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Wyslanie wiadomosci. Klient podaje wylacznie tresc - nadawca wynika z tokenu,
 * a czas ustawia serwer, wiec nie da sie podszyc pod druga strone ani cofnac daty.
 *
 * sharingId jest w sciezce dla REST-a, ale w ciele dla WebSocketa: w STOMP nie ma
 * sciezki zadania, wiec pole jest opcjonalne i uzywa go tylko kanal WS.
 */
public class SendMessageRequest {

    private Long sharingId;

    @NotBlank(message = "Wiadomosc nie moze byc pusta")
    @Size(max = 2000, message = "Wiadomosc moze miec najwyzej 2000 znakow")
    private String content;

    public Long getSharingId() {
        return sharingId;
    }

    public void setSharingId(Long sharingId) {
        this.sharingId = sharingId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
