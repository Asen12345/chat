import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { v4 as uuidv4 } from 'uuid';

interface Message {
    username: string;
    message: string;
    gptResponse: string;
    timestamp: string;
}

interface Chat {
    chatId: string;
    chatName: string;
}

interface ChatState {
    uid: string;
    chats: Chat[];
    messages: { [chatId: string]: Message[] };
    userAddress: string; // Новое поле для хранения адреса пользователя
    initializeUid: () => void;
    fetchChats: (uid: string) => Promise<void>;
    createChat: (uid: string, chatId: string, categories: string, model: string) => Promise<void>;
    fetchMessages: (uid: string, chatId: string) => Promise<void>;
    sendMessage: (uid: string, chatId: string, message: Message) => Promise<void>;
    setUserAddress: (address: string) => void; // Новый метод для установки адреса
    deleteChat: (uid: string, chatId: string) => Promise<void>;
}

export const useChatStore = create<ChatState>()(
    persist(
        (set, get) => ({
            uid: '',
            chats: [],
            messages: {},
            userAddress: '', // Инициализация нового поля

            initializeUid: () => {
                let storedUid = localStorage.getItem('uid');
                if (!storedUid) {
                    storedUid = uuidv4();
                    localStorage.setItem('uid', storedUid);
                }
                set({ uid: storedUid });
            },

            fetchChats: async (uid) => {
                try {
                    const response = await fetch(`https://meetmap.up.railway.app/get/chats/${uid}`);
                    if (response.ok) {
                        const data: Chat[] = await response.json();
                        set({ chats: data });
                    } else {
                        console.error(`Ошибка при получении чатов: ${response.status}`);
                    }
                } catch (error) {
                    console.error('Ошибка при запросе чатов:', error);
                }
            },

            createChat: async (uid, chatId, categories, model) => {
                try {
                    const response = await fetch(`https://meetmap.up.railway.app/create/chat/${uid}/${chatId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify({ categories, model }),
                    });
                    if (response.ok) {
                        // После успешного создания чата, можно обновить список чатов
                        await get().fetchChats(uid);
                    } else {
                        console.error(`Ошибка при создании чата: ${response.status}`);
                    }
                } catch (error) {
                    console.error('Ошибка при создании чата:', error);
                }
            },

            deleteChat: async (uid, chatId) => {
                try {
                    const response = await fetch(`https://meetmap.up.railway.app/delete/Chat/${uid}/${chatId}`, {
                        method: 'DELETE',
                    });
                    if (response.ok) {
                        // После успешного удаления чата, обновляем список чатов
                        const updatedChats = get().chats.filter(chat => chat.chatId !== chatId);
                        set({ chats: updatedChats });

                        // Также удаляем сообщения этого чата из состояния
                        const updatedMessages = { ...get().messages };
                        delete updatedMessages[chatId];
                        set({ messages: updatedMessages });
                    } else {
                        console.error(`Ошибка при удалении чата: ${response.status}`);
                    }
                } catch (error) {
                    console.error('Ошибка при удалении чата:', error);
                }
            },

            fetchMessages: async (uid, chatId) => {
                try {
                    const response = await fetch(`https://meetmap.up.railway.app/get/messages/${uid}/${chatId}`);
                    if (response.ok) {
                        const data: Message[] = await response.json();
                        set((state) => ({
                            messages: {
                                ...state.messages,
                                [chatId]: data,
                            },
                        }));
                    } else {
                        console.error(`Ошибка при получении сообщений: ${response.status}`);
                    }
                } catch (error) {
                    console.error('Ошибка при запросе сообщений:', error);
                }
            },

            sendMessage: async (uid, chatId, message) => {
                try {
                    const response = await fetch(`https://meetmap.up.railway.app/create/messages/${uid}/${chatId}`, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                        body: JSON.stringify(message),
                    });
                    if (response.ok) {
                        // После успешной отправки сообщения, можно обновить список сообщений
                        await get().fetchMessages(uid, chatId);
                    } else {
                        console.error(`Ошибка при отправке сообщения: ${response.status}`);
                    }
                } catch (error) {
                    console.error('Ошибка при отправке сообщения:', error);
                }
            },

            setUserAddress: (address) => {
                console.log(address)
                set({ userAddress: address });
            },
        }),
        {
            name: 'chat-store',
            partialize: (state) => ({
                uid: state.uid,
                chats: state.chats,
                messages: state.messages,
                userAddress: state.userAddress,
            }),
        }
    )
);