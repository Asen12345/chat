import styles from '@/styles/ui/input.module.scss'
import Image from 'next/image'
import { useState } from 'react';
import { useMask } from '@react-input/mask';

export default function Input(props: {
    type: string,
    marginTop?: string,
    label?: string,
    value?: string | number,
    onChange?: (value: string) => void,
    disabled?: boolean,
    placeholder?: string
}) {

    const [type, setType] = useState(props.type);

    const inputRef = useMask({ mask: '+7 (___) ___-__-__', replacement: { _: /\d/ } });

    return (
        <div className={styles['input-item']} style={{ marginTop: props.marginTop ? props.marginTop : '0px' }}>
            {props.label && <label>
                {props.label}
            </label>}
            <div className={styles['input-item__content']}>
                <input
                    ref={props.type === 'tel' ? inputRef : undefined}
                    onChange={(e) => props.onChange?.(e.target.value)}
                    type={type}
                    placeholder={props.placeholder ? props.placeholder : ''}
                    defaultValue={(props.value && props.value !== 0) ? props.value : ''}
                    className={`${(type === 'password') ? styles['password'] : ''} ${props.disabled ? styles['disabled'] : ''}`}
                    disabled={props.disabled}
                />
                {(props.type === 'password') && (
                    <div className={styles['input-icon']}>
                        {type === 'password' ?
                            <Image
                                src={require('@/icons/password-icon.svg')}
                                alt=""
                                onClick={e => setType('text')}
                            />
                            :
                            <Image
                                src={require('@/icons/password-hide.svg')}
                                alt=""
                                onClick={e => setType('password')}
                            />
                        }
                    </div>)}
            </div>
        </div >
    )
}
