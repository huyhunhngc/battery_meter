package io.github.ifa.glancewidget.model

import io.github.ifa.glancewidget.R
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val theme: ThemeType = ThemeType.FOLLOW_SYSTEM,
    val language: Language = Language.DEFAULT,
) {
    enum class Language(val code: String) {
        DEFAULT("sys"),
        ENGLISH("en"),
        FRENCH("fr"),
        VIETNAMESE("vi"),
        JAPANESE("ja");

        fun displayNameResId(): Int {
            return when (this) {
                ENGLISH -> R.string.english_language
                FRENCH -> R.string.french_language
                VIETNAMESE -> R.string.vietnamese_language
                JAPANESE -> R.string.japanese_language
                DEFAULT -> R.string.follow_system
            }
        }

        companion object {
            fun fromCode(code: String): Language {
                return entries.find { it.code == code } ?: DEFAULT
            }

            fun options(): List<String> {
                return entries.filter { it != DEFAULT }.map { it.code }.sortedBy { it }
            }
        }
    }
}
