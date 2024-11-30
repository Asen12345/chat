import React, { useEffect } from 'react';
import { useChatStore } from '@/store/chatStore';
import styles from "@/styles/chat.module.scss";
import { v4 as uuidv4 } from 'uuid';
import Button from "@/components-ui/button"
import AddIcon from "@/icons/add.svg"
import NavigationIcon from "@/icons/navigation.svg"
import LogoImg from "@/img/logo.svg"
import Image from 'next/image';
import Scrollbar from "react-scrollbars-custom";

interface ChatListProps {
    uid: string;
    onSelectChat: (chatId: string) => void;
}

const ChatList: React.FC<ChatListProps> = ({ uid, onSelectChat }) => {
    const { chats, fetchChats } = useChatStore();
    const createChat = useChatStore((state) => state.createChat);

    useEffect(() => {
        fetchChats(uid);
    }, [uid, fetchChats]);


    const handleCreateChat = async () => {
        const chatId = `chat_${uuidv4()}`;
        const chatName = `Чат ${uuidv4()}`;
        await createChat(uid, chatId, chatName);
    };

    return (
        <div className={styles['chat']}>
            <div className={`${styles['chat-header']} ${styles['list']}`}>
            <div className={styles["chat-header__img"]}>
                    <Image src={LogoImg} alt="" />
                </div>
                <div className={styles["chat-header__text"]}>
                    <h1>
                        Я здесь живу
                    </h1>
                    <h2 className={styles["chat-subtitle"]}>
                        Интеллектуальный AI чат-бот
                    </h2>
                </div>
            </div>
            <Scrollbar style={{ height: "70vh" }}>
                <div className={styles['chat-list']}>
                    <ul>
                        {chats.map((chat, index) => {
                            return (
                                <li className={styles['item']} key={chat.chatId} onClick={() => onSelectChat(chat.chatId)}>
                                    Чат №{index + 1}
                                </li>
                            );
                        })}
                    </ul>
                </div>
            </Scrollbar>
            <div className={styles['chat-footer']}>
                <Button icon={NavigationIcon} width={48} />
                <Button icon={AddIcon} value={"Создать чат"} onClick={handleCreateChat} />
            </div>
        </div>
    );
};

export default ChatList;