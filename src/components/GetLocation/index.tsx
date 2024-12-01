"use client";
import React, { useState } from 'react';
import MapComponent from "./Map"
import AddressForm from "./AddressForm"
import RightArrowIcon from "@/icons/right-arrow.svg"
import Button from "@/components-ui/button"
import styles from "@/styles/map.module.scss"

const GetLocation: React.FC<{ goToChat: () => void }> = ({ goToChat }) => {
    const [address, setAddress] = useState<string>('');

    const handleSearch = (searchAddress: string) => {
        setAddress(searchAddress);
    };

    return (
        <div>
            <div className={styles["chat-page"]}>
                <Button icon={RightArrowIcon} onClick={() => goToChat()} />
            </div>
            <AddressForm onSearch={handleSearch} />
            <MapComponent address={address} />
        </div>
    );
};

export default GetLocation;