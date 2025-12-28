package com.moodtracker.ui.theme

enum class AppTheme {
    SERENITY,
    SUNSET,
    FOREST,
    OCEAN;

    fun getDisplayName(): String = when (this) {
        SERENITY -> "Serenity"
        SUNSET -> "Sunset"
        FOREST -> "Forest"
        OCEAN -> "Ocean"
    }
}
