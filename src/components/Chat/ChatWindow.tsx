import React, { useEffect, useState } from 'react';
import { useChatStore } from '@/store/chatStore';
import styles from "@/styles/chat.module.scss";
import Input from "@/components-ui/input"
import Button from "@/components-ui/button"
import SendIcon from "@/icons/send.svg"
import BackIcon from "@/icons/back.svg"
import Scrollbar from "react-scrollbars-custom";
import TipsIcon from "@/icons/tips.svg"
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import Image from 'next/image';

interface ChatWindowProps {
    uid: string;
    chatId: string;
    openChatList: () => void;
}

const ChatWindow: React.FC<ChatWindowProps> = ({ uid, chatId, openChatList }) => {
    const { messages, fetchMessages, sendMessage, userAddress, chats } = useChatStore();
    const [newMessage, setNewMessage] = useState<string>('');
    const [selectedCategory, setSelectedCategory] = useState<number | null>(null);

    useEffect(() => {
        fetchMessages(uid, chatId);
    }, [uid, chatId, fetchMessages]);

    const handleSend = () => {
        if (newMessage.trim() === '') return;

        // Найдите чат по chatId для получения названия чата
        const currentChat = chats.find(chat => chat.chatId === chatId);
        const chatName = currentChat ? currentChat.chatName : 'Неизвестный чат';

        const message = {
            username: 'User',
            message: newMessage,
            gptResponse: '',
            character: `Я — помощник, который всегда готов найти для тебя нужную информацию, живу по адресу ${userAddress}! Я стараюсь быть максимально точным и вежливым, помогая находить ресурсы, адреса и контактные данные, включая номера телефонов, исходя из твоих запросов. Я быстро реагирую и всегда предоставляю полную информацию, которая может быть полезна, в том числе уточняю, как найти ближайшие к тебе места. Если ты ищешь конкретные адреса или телефоны, просто скажи, и я постараюсь дать все необходимые данные, учитывая твоё местоположение. Моя цель — предоставить тебе точную информацию, будь то адрес или номер телефона, чтобы ты мог легко найти нужное.`,
            model: "GigaChat",
            timestamp: new Date().toISOString(),
        };
        setNewMessage('');
        sendMessage(uid, chatId, message);
    };

    const settings = {
        infinite: false,
        slidesToShow: 2.5,
        slidesToScroll: 2,
        arrows: false,
    };

    const handleCategoryClick = (index: number) => {
        if (selectedCategory === index) {
            setSelectedCategory(null);
        } else {
            setSelectedCategory(index);
        }
    };

    return (
        <div className={`${styles['chat']} ${styles['white']}`}>
            <div className={`${styles['chat-header']} ${styles['window']}`}>
                <Button icon={BackIcon} width={48} onClick={openChatList} />
                <div className={styles["chat-header__text"]}>
                    <h4>
                        AI помощник
                    </h4>
                    <h5>
                        {(messages[chatId] !== undefined) ? messages[chatId].length : 0} сообщений с чат ботом
                    </h5>
                </div>
            </div>
            <Scrollbar style={{ height: "80vh" }}>
                <div className={styles['chat-window']}>
                    {messages[chatId]?.map((msg, index) => (
                        <div key={index}>
                            <div className={`${styles['chat-message']} ${styles['user']}`}>
                                <div className={styles["chat-message__text"]}>
                                    {msg.message}
                                </div>
                                <div className={styles["chat-message__date"]}>
                                    {msg.timestamp}
                                </div>
                            </div>
                            <div className={`${styles['chat-message']} ${styles['assistent']}`}>
                                <div className={styles["chat-message__text"]}>
                                    {msg.gptResponse === '' ? "Сообщение от AI помощника" : msg.gptResponse}
                                </div>
                                <div className={styles["chat-message__date"]}>
                                    {msg.timestamp}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </Scrollbar>
            <div className={`${styles['chat-footer']} ${styles['window']}`}>
                <Input
                    type="text"
                    value={newMessage}
                    onChange={(value) => setNewMessage(value)}
                    placeholder="Введите сообщение"
                />
                <Button color={"#fff"} backgroundColor={"rgba(149, 217, 254)"} icon={SendIcon} width={128} value='Отправить' onClick={handleSend} />
            </div>
        </div >
    );
};

export default ChatWindow;