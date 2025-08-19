package io.github.ifa.glancewidget.model

import io.github.ifa.glancewidget.R
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val theme: ThemeType = ThemeType.FOLLOW_SYSTEM,
    val themeColor: ThemeTypeColor = ThemeTypeColor.System,
    val syncColorEnabled: Boolean = true,
    val language: Language = Language.DEFAULT,
    val notificationSetting: NotificationSetting = NotificationSetting()
) {
    @Serializable
    data class NotificationSetting(
        val batteryAlert: Boolean = false,
        val showPairedDevices: Boolean = false
    )

    enum class Language(val code: String) {
        DEFAULT("sys"),
        ENGLISH("en"),
        FRENCH("fr"),
        VIETNAMESE("vi"),
        JAPANESE("ja"),
        KOREAN("ko"),
        SPANISH("es");

        fun displayNameResId(): Int {
            return when (this) {
                ENGLISH -> R.string.english_language
                FRENCH -> R.string.french_language
                VIETNAMESE -> R.string.vietnamese_language
                JAPANESE -> R.string.japanese_language
                DEFAULT -> R.string.follow_system
                KOREAN -> R.string.korean_language
                SPANISH -> R.string.spanish_language
            }
        }

        companion object {
            fun fromCode(code: String): Language {
                return entries.find { it.code == code } ?: DEFAULT
            }

            fun options(): List<String> {
                return entries.filter { it != DEFAULT }.map { it.code }.sortedBy { it }
                    .toPersistentList().add(0, DEFAULT.code)
            }
        }
    }
}
