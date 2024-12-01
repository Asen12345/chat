"use client";
import React, { useState, useEffect } from 'react';
import ChatList from './ChatList';
import ChatWindow from './ChatWindow';
import { useChatStore } from '../../store/chatStore';

interface ChatProps {
    goBack: () => void;
}

const Chat: React.FC<ChatProps> = ({ goBack }) => {
    const [selectedChatId, setSelectedChatId] = useState<string>('');
    const uid = useChatStore((state) => state.uid);
    const initializeUid = useChatStore((state) => state.initializeUid);
    const fetchChats = useChatStore((state) => state.fetchChats);

    useEffect(() => {
        initializeUid();
    }, [initializeUid]);

    useEffect(() => {
        if (uid) {
            fetchChats(uid);
        }
    }, [fetchChats, uid]);

    return (
        selectedChatId
            ? <ChatWindow uid={uid} chatId={selectedChatId} openChatList={() => setSelectedChatId('')} />
            : uid && <ChatList uid={uid} onSelectChat={setSelectedChatId} goBack={goBack} />
    )
};

export default Chat;