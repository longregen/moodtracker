package com.moodtracker.ui.theme

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object Animations {
    // Card press animation constants
    const val CARD_PRESS_SCALE = 0.97f
    const val CARD_NORMAL_SCALE = 1f

    // Animation durations
    const val ANIMATION_DURATION_SHORT = 150
    const val ANIMATION_DURATION_MEDIUM = 300
    const val ANIMATION_DURATION_LONG = 500

    // Spring animation spec for card press
    fun <T> cardPressSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )
}
