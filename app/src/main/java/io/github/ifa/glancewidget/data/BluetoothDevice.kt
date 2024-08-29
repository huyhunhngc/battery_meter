package io.github.ifa.glancewidget.data

data class BluetoothDevice(
    val name: String?,
    val model: SpeakerModel,
    val address: String?,
    val batteryInPercentage: Int,
    val batteryInMinutes: Int,
    val isConnected: Boolean
)

enum class SpeakerModel(val model: String) {
    AIR_DOTS("Xiaomi Air Dots"), AIR_DOTS_PRO_1("Xiaomi Air Dots Pro"), AIR_DOTS_PRO_2("Xiaomi Air Dots Pro 2"), XIAOMI_WIRELESS_BLUETOOTH_SPEAKER("Xiaomi Wireless BL Speaker"),
    MI_SPEAKER("Mi Speaker"), MI_POCKET_SPEAKER_2("Mi Pocket Speaker 2"), TWS_HONOR_CHOICE("TWS Honor Choice"),
    AIR_DOTS_2_SE("Xiaomi Air Dots 2 SE"), NICEBOY_HIVE("Niceboy HIVE"), NICEBOY_HIVE_PODSIE("Niceboy HIVE Podsie"), AIR_DOTS_S("Xiaomi Air Dots S"),
    AIR_DOTS_3("Xiaomi Air Dots 3") , AIR_DOTS_PRO_3("Xiaomi Air Dots 3 Pro"), FLIPBUDS_PRO("Xiaomi FlipBuds Pro"), REDMI_BUDS_3_LITE("Redmi Buds 3 Lite"), REDMI_BUDS_3_PRO("Redmi Buds 3 Pro"),
    XIAOMI_BUDS_3T_PRO("Xiaomi Buds 3T Pro"), XIAOMI_BUDS_3("Xiaomi Buds 3"), MI_TWS_BASIC_2S("Mi TWS Basic 2S"), MI_TWS_2_PRO("Mi TWS 2 Pro"), REDMI_BUDS_ESSENTIAL("Redmi Buds Essential"), UNKNOWN("")
}