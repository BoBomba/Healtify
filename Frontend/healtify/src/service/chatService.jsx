import axios from "axios";

const API_URL = 'http://localhost:8080/api/chat';

/** Celowo mało. Docelowo 20-30 (backend przycina to do 50). */
export const CHAT_PAGE_SIZE = 10;

const authConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
    }
});

/* Rozmowy z obu stron naraz. */
export const GetConversations = async () => {
    const response = await axios.get(`${API_URL}/conversations`, authConfig());
    return Array.isArray(response.data) ? response.data : [];
}

/* Naglowek czatu */
export const GetConversation = async (sharingId) => {
    const response = await axios.get(`${API_URL}/${sharingId}`, authConfig());
    return response.data;
}

/**
 * Porcja historii. 
 * Zwraca { messages: [rosnąco po czasie], hasMore }.
 */
export const GetMessages = async (sharingId, before = null, limit = CHAT_PAGE_SIZE) => {
    const response = await axios.get(`${API_URL}/${sharingId}/messages`, {
        ...authConfig(),
        params: before === null ? { limit } : { before, limit }
    });
    return {
        messages: Array.isArray(response.data?.messages) ? response.data.messages : [],
        hasMore: Boolean(response.data?.hasMore)
    };
}

/**
 * Wysylka po HTTP. Normalnie jest przez WebSocket (chatSocket.jsx)
 * tylko wtedy, gdy gniazdo jest rozłączone, żeby wiadomosc nie przepadła.
 */
export const SendMessage = async (sharingId, content) => {
    const response = await axios.post(`${API_URL}/${sharingId}/messages`, { content }, authConfig());
    return response.data;
}

/* Otwarcie rozmowy zeruje licznik po naszej stronie. */
export const MarkConversationRead = async (sharingId) => {
    await axios.post(`${API_URL}/${sharingId}/read`, {}, authConfig());
}
