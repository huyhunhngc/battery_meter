package com.dotsdev.material3color

import android.os.Build

fun isSupportedDynamicColor(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}
