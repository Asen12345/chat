"use client";
import React, { useState } from 'react';
import MapComponent from "./Map"
import AddressForm from "./AddressForm"
import RightArrowIcon from "@/icons/right-arrow.svg"
import Button from "@/components-ui/button"

const GetLocation: React.FC = () => {
    const [address, setAddress] = useState<string>('');

    const handleSearch = (searchAddress: string) => {
        setAddress(searchAddress);
    };

    return (
        <div>
            <div className="chat-page">
                <Button icon={RightArrowIcon} />
            </div>
            <AddressForm onSearch={handleSearch} />
            <MapComponent address={address} />
        </div>
    );
};

export default GetLocation;