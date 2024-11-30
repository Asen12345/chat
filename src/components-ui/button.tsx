import styles from '@/styles/ui/button.module.scss'
import Image from 'next/image'

export default function Button(props: {
    value?: string,
    marginTop?: string,
    backgroundColor?: string,
    color?: string,
    icon?: any,
    width?: number,
    disabled?: boolean,
    onClick?: () => void
}) {
    return (
        <button className={`${styles["button-item"]} ${(props.disabled) ? styles["disabled"] : ''}`} style={{ maxWidth: props.width ? props.width + "px" : 'revert-layer', color: props.color ? props.color : '', marginTop: props.marginTop ? props.marginTop : '', backgroundColor: props.backgroundColor ? props.backgroundColor : '' }} onClick={() => props.onClick?.()}>
            {props.icon && <Image alt='' src={props.icon} />} {props.value ? props.value : ''}
        </button>
    )
}