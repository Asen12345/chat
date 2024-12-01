import React, { useEffect, useState } from 'react';
import { useChatStore } from '@/store/chatStore';
import styles from "@/styles/chat.module.scss";
import { v4 as uuidv4 } from 'uuid';
import Button from "@/components-ui/button";
import AddIcon from "@/icons/add.svg";
import NavigationIcon from "@/icons/navigation.svg";
import LogoImg from "@/img/logo.svg";
import Image from 'next/image';
import Scrollbar from "react-scrollbars-custom";
import TreeIcon from "@/icons/tree.svg";
import TheatreIcon from "@/icons/theatre.svg";
import EcologyIcon from "@/icons/ecology.svg";
import SchoolIcon from "@/icons/school.svg";
import BookIcon from "@/icons/book.svg";
import PhoneIcon from "@/icons/phone.svg";

interface ChatListProps {
    uid: string;
    onSelectChat: (chatId: string) => void;
    goBack: () => void;
}

const ChatList: React.FC<ChatListProps> = ({ uid, onSelectChat, goBack }) => {
    const { chats, fetchChats, createChat, deleteChat } = useChatStore();
    const [openCategory, setOpenCategory] = useState(false);
    const [loader, setLoader] = useState(false)

    const categories = [
        {
            title: "Благоустройство, ЖКХ и уборка дорог",
            icon: TreeIcon
        },
        {
            title: "Поиск контактов, основанный на Базе Контактов Санкт-Петербурга",
            icon: BookIcon
        },
        {
            title: "Поиск релевантной информации, ответ на вопрос основанный на Базе Знаний Санкт-Петербурга",
            icon: PhoneIcon
        },
        {
            title: "Образование, Детские сады и Школы",
            icon: SchoolIcon
        },
        {
            title: "Раздельный сбор",
            icon: EcologyIcon
        },
        {
            title: "Афиша, Красивые места",
            icon: TheatreIcon
        }
    ];

    useEffect(() => {
        fetchChats(uid);
    }, [uid, fetchChats]);

    const handleCreateChat = async (categoryTitle: string) => {
        setLoader(true)
        const model = "GigaChat";
        const chatId = `chat_${uuidv4()}`
        await createChat(uid, chatId, categoryTitle, model).finally(() => {
            setLoader(false)
            setOpenCategory(false);
        });
    };

    const handleDeleteChat = async (e: React.MouseEvent, chatId: string) => {
        e.stopPropagation(); // Предотвращаем всплытие события
        await deleteChat(uid, chatId);
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
                {loader &&
                    <div className={styles["profile-main-loader"]}>
                        <div className={styles["loader"]}>
                            <svg className={styles["circular-loader"]} viewBox="25 25 50 50" >
                                <circle className={styles["loader-path"]} cx="50" cy="50" r="20" fill="none" stroke="#70c542" stroke-width="2" />
                            </svg>
                        </div>
                    </div>}
                {openCategory ?
                    !loader && <div className={styles["categories-list"]}>
                        {categories.map((category, index) => {
                            return (
                                <Button
                                    icon={category.icon}
                                    color='#3C92C3'
                                    value={category.title}
                                    key={index}
                                    onClick={() => handleCreateChat(category.title)}
                                />
                            )
                        })}
                    </div>
                    :
                    <div className={styles['chat-list']}>
                        {chats.length !== 0 ?
                            <ul>
                                {chats.map((chat, index) => {
                                    return (
                                        <li className={styles['item']} key={chat.chatId} onClick={() => onSelectChat(chat.chatId)}>
                                            <div
                                                className={styles["item-delete"]}
                                                onClick={(e) => handleDeleteChat(e, chat.chatId)}
                                            >
                                                <Image width={16} height={16} src={require("@/icons/trash.svg")} alt='' />
                                            </div>
                                            {chat.chatName}
                                        </li>
                                    );
                                })}
                            </ul>
                            :
                            <p>
                                У вас пока что нету чатов, создайте новый
                            </p>
                        }
                    </div>
                }
            </Scrollbar>
            <div className={styles['chat-footer']}>
                <Button icon={NavigationIcon} width={48} onClick={goBack} />
                <Button icon={AddIcon} value={"Создать чат"} onClick={() => setOpenCategory(true)} />
            </div>
        </div>
    );
};

export default ChatList;