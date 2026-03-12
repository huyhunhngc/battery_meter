package io.github.ifa.glancewidget.utils

object Constants {
    const val MIN_DESIGN_CAPACITY = 1000
    const val MAX_DESIGN_CAPACITY = 20000
    const val MAH_UNIT = "mAh"
    const val UAH_UNIT = "μAh"
    const val MA_UNIT = "mA"
    const val WATT_UNIT = "watts"
    const val ANDROID_SETTING_PACKAGE = "com.android.settings"
    const val BLUETOOTH_SETTING_CLASS = "com.android.settings.bluetooth.BluetoothSettings"
    const val IFA_TEAM_URL = "https://ifateam.dev"
    const val STORE_APP_URL = "https://play.google.com/store/apps/details?id=io.github.ifa.glancewidget"
    const val IFA_GITHUB_URL = "https://github.com/huyhunhngc/battery_meter"
    const val IFA_SUPPORT_URL = "https://chat.whatsapp.com/Lp6sTeZCCzGJnBa3CFo1Xo"
    const val IFA_LICENSES_URL = "https://www.termsfeed.com/live/82a28b83-ca15-4847-a3f4-6b85508f6060"
    const val DEFAULT_MAX_WATTS_CHARGE = 65.0f
    const val DEFAULT_MAX_COLLECT_CURRENT = 500
    const val NUMBER_OF_CYCLES_PATH = "/sys/class/power_supply/battery/cycle_count"
}