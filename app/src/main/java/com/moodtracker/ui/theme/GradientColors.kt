package com.moodtracker.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object GradientColors {
    @Composable
    @ReadOnlyComposable
    fun primaryGradient(
        primary: Color,
        secondary: Color
    ): Brush {
        return Brush.linearGradient(
            colors = listOf(primary, secondary),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
        )
    }

    @Composable
    @ReadOnlyComposable
    fun cardAccentGradient(
        primary: Color,
        secondary: Color
    ): Brush {
        return Brush.verticalGradient(
            colors = listOf(primary, secondary)
        )
    }

    @Composable
    @ReadOnlyComposable
    fun buttonGradient(
        primary: Color,
        secondary: Color
    ): Brush {
        return Brush.horizontalGradient(
            colors = listOf(primary, secondary)
        )
    }
}
