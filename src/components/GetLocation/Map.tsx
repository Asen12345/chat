"use client";
import React, { useEffect, useState } from 'react';
import { YMaps, Map, Placemark } from '@pbe/react-yandex-maps';
import { useChatStore } from '@/store/chatStore';

interface MapComponentProps {
    address: string;
}

const MapComponent: React.FC<MapComponentProps> = ({ address }) => {
    const [center, setCenter] = useState<[number, number]>([55.751244, 37.618423]); // Москва по умолчанию
    const [placemark, setPlacemark] = useState<[number, number] | null>(null);
    const [userLocation, setUserLocation] = useState<[number, number] | null>(null);
    const setUserAddress = useChatStore((state) => state.setUserAddress);
    const userAddress = useChatStore((state) => state.userAddress);

    // Получение геолокации пользователя
    useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const { latitude, longitude } = position.coords;
                    const userCoords: [number, number] = [latitude, longitude];
                    setCenter(userCoords);
                    setUserLocation(userCoords);
                    setPlacemark(userCoords);
                },
                (error) => {
                    console.error('Ошибка получения геолокации:', error);
                }
            );
        } else {
            console.error('Геолокация не поддерживается этим браузером.');
        }
    }, []);

    // Функция для поиска адреса
    const geocodeAddress = async (address: string): Promise<[number, number] | null> => {
        const apiKey = 'eb69c8ca-0f70-4db1-965c-dcd6a2c802fd';
        const encodedAddress = encodeURIComponent(address);
        const geocodeUrl = `https://geocode-maps.yandex.ru/1.x/?apikey=${apiKey}&format=json&geocode=${encodedAddress}`;

        try {
            const response = await fetch(geocodeUrl);
            const data = await response.json();
            const pos = data.response.GeoObjectCollection.featureMember[0].GeoObject.Point.pos.split(' ').map(Number);
            if (pos.length === 2) {
                return [pos[1], pos[0]];
            }
            return null;
        } catch (error) {
            console.error('Ошибка геокодирования:', error);
            return null;
        }
    };

    useEffect(() => {
        const fetchCoords = async () => {
            const coords = await geocodeAddress(address);
            if (coords) {
                setCenter(coords);
                setPlacemark(coords);
            }
        };

        if (address && address !== '') {
            fetchCoords();
            setUserAddress(address);
        }
    }, [address, setUserAddress]);

    // Дополнительный эффект для обновления карты при изменении userAddress из хранилища
    useEffect(() => {
        if (userAddress && userAddress !== address) {
            const fetchCoords = async () => {
                const coords = await geocodeAddress(userAddress);
                if (coords) {
                    setCenter(coords);
                    setPlacemark(coords);
                }
            };
            fetchCoords();
        }
    }, [userAddress]);

    return (
        <YMaps
            query={{
                apikey: 'eb69c8ca-0f70-4db1-965c-dcd6a2c802fd',
                lang: 'ru_RU',
            }}
        >
            <Map
                defaultState={{ center, zoom: 12 }}
                state={{ center, zoom: 12 }}
                style={{ width: '100vw', height: '100vh' }}
                controls={['zoomControl']}
            >
                {placemark && <Placemark geometry={placemark} />}
            </Map>
        </YMaps>
    );
};

export default MapComponent;