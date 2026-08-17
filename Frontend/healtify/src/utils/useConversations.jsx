import { useCallback, useEffect, useMemo, useState } from 'react';
import { GetConversations } from '../service/chatService';
import { onChatMessage } from '../service/chatSocket';

/**
 * Czaty dla panelu udostepniania.
 *
 * Robi dwie rzeczy:
 * - daje sharingId potrzebne do linku czatu przy danym userze
 *   (/api/sharing i /api/doctor znaja tylko doctorId / userId),
 * - licznik nieprzeczytanych.
 *
 * Licznik podbija się na zywo z gniazda, lista jest już
 * podpięta pod tę samą kolejkę co otwarty czat.
 *
 * @param role 'PATIENT' albo 'DOCTOR' - którą strona jest konto na tej stronie.
 *             Lista z backendu przychodzi cala i odsiewamy ja tutaj.
 */
export function useConversations(role) {
    const [conversations, setConversations] = useState([]);

    const refresh = useCallback(() => {
        GetConversations()
            .then((data) => setConversations(data))
            .catch((error) => console.log(error));
    }, []);

    useEffect(() => {
        refresh();
    }, [refresh]);

    useEffect(() => onChatMessage((incoming) => {
        setConversations((previous) => {
            // Wiadomość z rozmowy, której nie ma jeszcze na liscie - dociągamy listę od nowa.
            if (!previous.some((item) => item.sharingId === incoming.sharingId)) {
                refresh();
                return previous;
            }
            return previous.map((item) => {
                if (item.sharingId !== incoming.sharingId) return item;
                // Własna wiadomość nie jest nieprzeczytana.
                const unreadCount = incoming.sender === item.myRole
                    ? item.unreadCount
                    : item.unreadCount + 1;
                return { ...item, unreadCount, lastMessageAt: incoming.sentAt };
            });
        });
    }), [refresh]);

    /* Rozmowy tej strony, pod id partnera */
    const byPartner = useMemo(() => {
        const map = new Map();
        conversations
            .filter((item) => item.myRole === role)
            .forEach((item) => map.set(role === 'PATIENT' ? item.doctorId : item.patientId, item));
        return map;
    }, [conversations, role]);

    /* Wyzerowanie licznika po wejsciu w czat, bez czekania na serwer. */
    const clearUnread = useCallback((sharingId) => {
        setConversations((previous) => previous.map((item) =>
            item.sharingId === sharingId ? { ...item, unreadCount: 0 } : item
        ));
    }, []);

    return { byPartner, refresh, clearUnread };
}
