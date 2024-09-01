package io.github.ifa.glancewidget.model

import androidx.annotation.DrawableRes
import io.github.ifa.glancewidget.R

enum class DeviceType(@DrawableRes val icon: Int) {
    PHONE(R.drawable.ic_smartphone),
    HEADSET(R.drawable.ic_headphones),
    POINTING(R.drawable.ic_mouse),
    KEYBOARD(R.drawable.ic_keyboard),
    OTHER(R.drawable.ic_bluetooth);

    companion object {
        fun fromClass(deviceClass: Int): DeviceType {
            return when (deviceClass) {
                0x0200 -> PHONE
                0x0404 -> HEADSET
                0x0580 -> POINTING
                0x0540 -> KEYBOARD
                else -> OTHER
            }
        }
    }
}