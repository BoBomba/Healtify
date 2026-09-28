import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import '../css/dashboard.css';
import '../css/data.css';
import '../css/calendar.css';
import '../css/doctor.css';
import '../css/chat.css';
import Nav from '../Components/Nav';
import { validateToken } from '../service/authService';
import {
    GetConversation,
    GetMessages,
    MarkConversationRead,
    SendMessage,
} from '../service/chatService';
import { onChatError, onChatMessage, sendOverSocket } from '../service/chatSocket';
import { formatAppointmentDateTime } from '../utils/appointmentUtils';

/* Na tyle blisko dołu, by nowa wiadomosc mogla dociagnac widok bez wyrywania z czytania. */
const STICK_TO_BOTTOM_PX = 80;

/**
 * Czat pacjent - lekarz. Jedna strona dla obu: rozmowę wskazuje sharingId z adresu,
 * backend mówi, którą stroną jesteśmy (pole myRole), 
 * zmienia kolor przycisku wysylki i miejsce, gdzie wraca powrot.
 *
 * Historia jest stronicowana: Nowe wiadomosci przychodza WebSocketem, ta sama kolejka obsluguje obie strony.
 */
function ChatPage() {
    const { sharingId } = useParams();
    const navigate = useNavigate();

    const [conversation, setConversation] = useState(null);
    const [messages, setMessages] = useState([]);
    const [hasMore, setHasMore] = useState(false);
    const [loadingOlder, setLoadingOlder] = useState(false);
    const [draft, setDraft] = useState('');
    const [message, setMessage] = useState('');
    const [unavailable, setUnavailable] = useState(false);

    const scrollRef = useRef(null);
    /**
     * 'bottom' albo zapamiętane wymiary sprzed doładowania starszych,
     * zeby bylo responsywne
     */
    const scrollActionRef = useRef('bottom');
    /** Rola w domknieciach nasluchu, stan bylby tam zamrozony z pierwszego renderu. */
    const roleRef = useRef(null);

    // --- wejscie w rozmowe ---

    useEffect(() => {
        let cancelled = false;

        async function open() {
            await validateToken();
            try {
                const meta = await GetConversation(sharingId);
                const page = await GetMessages(sharingId);
                if (cancelled) return;

                roleRef.current = meta.myRole;
                setConversation(meta);
                setMessages(page.messages);
                setHasMore(page.hasMore);
                scrollActionRef.current = 'bottom';
                MarkConversationRead(sharingId).catch((error) => console.log(error));
            } catch (error) {
                console.log(error);
                if (cancelled) return;
                setUnavailable(true);
            }
        }

        open();
        return () => { cancelled = true; };
    }, [sharingId]);

    // --- wiadomości na żywo ---

    useEffect(() => onChatMessage((incoming) => {
        if (String(incoming.sharingId) !== String(sharingId)) return;

        // Sprawdzamy PRZED nowym dymkiem - bo po wysokosc jest już inna i by sie rozjezdzalo.
        const element = scrollRef.current;
        const atBottom = element
            ? element.scrollHeight - element.scrollTop - element.clientHeight < STICK_TO_BOTTOM_PX
            : true;

        setMessages((previous) => previous.some((item) => item.messageId === incoming.messageId)
            // Wiadomosc wyslana z tej karty wraca do nas gniazdem - to nie duplikat do dopisania.
            ? previous
            : [...previous, incoming]);

        if (atBottom) {
            scrollActionRef.current = 'bottom';
        }
        if (incoming.sender !== roleRef.current) {
            MarkConversationRead(sharingId).catch((error) => console.log(error));
        }
    }), [sharingId]);

    useEffect(() => onChatError((failure) => {
        setMessage(failure.message || 'Nie udało się wysłać wiadomości.');
    }), []);

    // --- scroll ---

    useLayoutEffect(() => {
        const element = scrollRef.current;
        const action = scrollActionRef.current;
        scrollActionRef.current = null;
        if (!element || !action) return;

        if (action === 'bottom') {
            element.scrollTop = element.scrollHeight;
            return;
        }
        // Doladowanie starszych: trzymamy w tym samym miejscu rozmowy, przesuwajac scroll
        // o tyle, o ile lista urosla u gory.
        element.scrollTop = element.scrollHeight - action.previousHeight + action.previousTop;
    }, [messages]);

    const handleLoadOlder = async () => {
        if (loadingOlder || messages.length === 0) return;

        const element = scrollRef.current;
        setLoadingOlder(true);
        try {
            const page = await GetMessages(sharingId, messages[0].messageId);
            scrollActionRef.current = element
                ? { previousHeight: element.scrollHeight, previousTop: element.scrollTop }
                : null;
            setMessages((previous) => [...page.messages, ...previous]);
            setHasMore(page.hasMore);
        } catch (error) {
            console.log(error);
            setMessage('Nie udało się pobrać starszych wiadomości.');
        } finally {
            setLoadingOlder(false);
        }
    };

    // --- wysylanie ---

    const handleSend = async (event) => {
        event.preventDefault();
        const content = draft.trim();
        if (content === '') return;

        setDraft('');
        setMessage('');
        scrollActionRef.current = 'bottom';

        // Gniazdo to droga podstawowa - dymek dojdzie pushem, tym samym, co do drugiej strony.
        if (sendOverSocket(Number(sharingId), content)) return;

        // Gniazdo akurat rozlaczone: wysyłamy HTTP-em i dokladamy dymek sami, bo push po tej drodze do nas nie wroci.
        try {
            const sent = await SendMessage(sharingId, content);
            setMessages((previous) => previous.some((item) => item.messageId === sent.messageId)
                ? previous
                : [...previous, sent]);
        } catch (error) {
            console.log(error);
            setMessage(error.response?.data?.message || 'Nie udało się wysłać wiadomości.');
            setDraft(content);
        }
    };


    const goBack = () => navigate(
        conversation?.myRole === 'DOCTOR' ? '/doctor/sharing' : '/sharing'
    );

    if (unavailable) {
        return (
            <div className="dashboard">
                <Nav />
                <main>
                    <div className="chat-page">
                        <div className="datablock chat-box chat-box-empty">
                            <p>Ta rozmowa nie jest dostępna. Dostęp do danych mógł zostać cofnięty.</p>
                            <button type="button" className="modal-btn secondary small" onClick={goBack}>
                                Wróć
                            </button>
                        </div>
                    </div>
                </main>
            </div>
        );
    }

    // Kolor mojej strony rozmowy: czerwony dla pacjenta, niebieski dla lekarza.
    const myRoleClass = conversation?.myRole === 'DOCTOR' ? 'doctor' : 'patient';

    return (
        <div className="dashboard">
            <Nav />
            <main>
                <div className="chat-page">
                    <div className="datablock chat-box">
                        <div className="chat-header">
                            <button
                                type="button"
                                className="chat-back"
                                onClick={goBack}
                                aria-label="Wróć do udostępniania"
                            >
                                {/* Szewron rysowany */}
                                <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                                    <polyline points="15,5 8,12 15,19" />
                                </svg>
                            </button>
                            <div className="chat-header-main">
                                <strong>{conversation?.partnerName || 'Rozmowa'}</strong>
                                <span className="chat-header-sub">
                                    {conversation?.myRole === 'DOCTOR' ? 'pacjent' : 'lekarz'}
                                </span>
                            </div>
                        </div>

                        {message && <div id="messages" className="chat-message">{message}</div>}

                        <div className="chat-scroll" ref={scrollRef}>
                            {hasMore && (
                                <button
                                    type="button"
                                    className="chat-older"
                                    onClick={handleLoadOlder}
                                    disabled={loadingOlder}
                                >
                                    <span className="chat-chevron">&#8963;</span>
                                    {loadingOlder ? 'Wczytuję...' : 'Starsze wiadomości'}
                                </button>
                            )}

                            {conversation && messages.length === 0 && (
                                <p className="chat-empty">Brak wiadomości. Napisz pierwszą.</p>
                            )}

                            {messages.map((item) => (
                                <div
                                    key={item.messageId}
                                    className={`chat-row ${item.sender === conversation?.myRole ? 'mine' : 'theirs'}`}
                                >
                                    <div className={`chat-bubble ${item.sender === 'DOCTOR' ? 'doctor' : 'patient'}`}>
                                        <span className="chat-text">{item.content}</span>
                                        {/* Data + godzina, format ten sam co przy wizytach. */}
                                        <span className="chat-time">
                                            {formatAppointmentDateTime(item.sentAt)}
                                        </span>
                                    </div>
                                </div>
                            ))}
                        </div>

                        <form className="chat-composer" onSubmit={handleSend}>
                            <input
                                type="text"
                                placeholder="Napisz wiadomość..."
                                value={draft}
                                onChange={(e) => setDraft(e.target.value)}
                                maxLength={2000}
                                disabled={!conversation}
                            />
                            <button
                                type="submit"
                                className={`chat-send ${myRoleClass}`}
                                disabled={!conversation || draft.trim() === ''}
                            >
                                Wyślij
                            </button>
                        </form>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default ChatPage;
