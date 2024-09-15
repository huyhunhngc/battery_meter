package io.github.ifa.glancewidget.model

import androidx.annotation.DrawableRes
import io.github.ifa.glancewidget.R

enum class DeviceType(@DrawableRes val icon: Int) {
    PHONE(R.drawable.ic_smartphone),
    OTHER_PHONE(R.drawable.ic_smartphone),
    WATCH(R.drawable.ic_watch),
    HEADSET(R.drawable.ic_headphones),
    MICROPHONE(R.drawable.ic_mic),
    POINTING(R.drawable.ic_mouse),
    KEYBOARD(R.drawable.ic_keyboard),
    TOY(R.drawable.ic_stadia_controller),
    LAPTOP(R.drawable.ic_laptop_windows),
    DESKTOP(R.drawable.ic_desktop_windows),
    OTHER(R.drawable.ic_bluetooth);

    companion object {
        fun fromClass(deviceClass: Int): DeviceType {
            return when (deviceClass) {
                0 -> PHONE
                0x0104 -> DESKTOP
                0x010C -> LAPTOP
                in 0x0200..0x0214 -> OTHER_PHONE
                0x0410 -> MICROPHONE
                0x0404 -> HEADSET
                0x0580 -> POINTING
                0x0540 -> KEYBOARD
                0x0704 -> WATCH
                in 0x0800..0x0814 -> TOY
                else -> OTHER
            }
        }
    }
}