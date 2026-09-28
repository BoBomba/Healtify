import { Client } from '@stomp/stompjs';

const WS_URL = 'ws://localhost:8080/ws';

/**
 * Klient STOMP czatu - jedno polaczenie na cala aplikacje.
 *
 * Gniazdo subskrybuje prywatna kolejke konta (/user/queue/chat), 
 * na którą lecą wiadomości ze wszystkich rozmów.
 * Jednym połączeniem obslugujemy i otwarty czat, i liczniki nieprzeczytanych.
 *
 * Polaczenie zyje tak dlugo, jak dlugo ktokolwiek go słucha,
 * ostatni odsubowany je zamyka, żeby nie wisiało po wyjsciu z czatu.
 */

let client = null;
const messageHandlers = new Set();
const errorHandlers = new Set();

function notify(handlers, payload) {
    handlers.forEach((handler) => {
        try {
            handler(payload);
        } catch (error) {
            // Error jednego nie może uwalic pozostalych ani całego gniazda.
            console.log(error);
        }
    });
}

// ---------------------------

function ensureClient() {
    if (client) return client;

    client = new Client({
        brokerURL: WS_URL,
        reconnectDelay: 5000,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,

        // Naglowki budowane przed KAŻDYM połączeniem,
        beforeConnect: () => {
            client.connectHeaders = {
                Authorization: `Bearer ${localStorage.getItem('token')}`
            };
        },

        onConnect: () => {
            client.subscribe('/user/queue/chat', (frame) => {
                notify(messageHandlers, JSON.parse(frame.body));
            });
            client.subscribe('/user/queue/errors', (frame) => {
                notify(errorHandlers, JSON.parse(frame.body));
            });
        },

        onStompError: (frame) => {
            console.log('Chat STOMP error:', frame.headers?.message, frame.body);
        }
    });

    client.activate();
    return client;
}

function releaseIfUnused() {
    if (client && messageHandlers.size === 0 && errorHandlers.size === 0) {
        client.deactivate();
        client = null;
    }
}

// ---------------------------

/**
 * Nasłuch nowych wiadomości. 
 * PAMIETAJ: Zwraca funkcję odsubowania bo inaczej po wyjściu z czatu zostaje odbiorca.
 */
export function onChatMessage(handler) {
    messageHandlers.add(handler);
    ensureClient();
    return () => {
        messageHandlers.delete(handler);
        releaseIfUnused();
    };
}

/* Nasluch bledow wysylki (/user/queue/errors). */
export function onChatError(handler) {
    errorHandlers.add(handler);
    ensureClient();
    return () => {
        errorHandlers.delete(handler);
        releaseIfUnused();
    };
}

/**
 * Wysylka wiadomosci gniazdem.
 * Zwraca false, gdy polaczenia nie ma - wtedy wolający ma wyslac ja REST-em (chatService.jsx).
 */
export function sendOverSocket(sharingId, content) {
    const active = ensureClient();
    if (!active.connected) {
        return false;
    }
    active.publish({
        destination: '/app/chat.send',
        body: JSON.stringify({ sharingId, content })
    });
    return true;
}
