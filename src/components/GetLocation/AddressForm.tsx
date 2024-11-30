"use client";
import React, { useState, useEffect } from 'react';
import Input from "@/components-ui/input";
import Button from "@/components-ui/button";
import styles from "@/styles/map.module.scss"
import PointIcon from "@/icons/point.svg"
import { useChatStore } from '@/store/chatStore';

interface AddressFormProps {
    onSearch: (address: string) => void;
}

const AddressForm: React.FC<AddressFormProps> = ({ onSearch }) => {
    const userAddress = useChatStore((state) => state.userAddress);
    const setUserAddress = useChatStore((state) => state.setUserAddress);
    const [address, setAddress] = useState<string>(userAddress);

    useEffect(() => {
        setAddress(userAddress);
    }, [userAddress]);

    const handleSubmit = () => {
        if (address.trim() === '') return;
        onSearch(address);
        setUserAddress(address);
    }

    return (
        <div className={styles["map-search"]}>
            <Input
                type="text"
                value={address}
                onChange={(value) => setAddress(value)}
                placeholder="Введите адрес"
            />
            <Button icon={PointIcon} value="Найти" color='#3C92C3' width={130} onClick={handleSubmit} />
        </div>
    );
};

export default AddressForm;