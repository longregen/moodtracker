package com.moodtracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext

// CompositionLocal for current theme
val LocalAppTheme = compositionLocalOf { AppTheme.SERENITY }

// Serenity Color Schemes
private val SerenityLightColorScheme = lightColorScheme(
    primary = serenity_light_primary,
    onPrimary = serenity_light_onPrimary,
    primaryContainer = serenity_light_primaryContainer,
    onPrimaryContainer = serenity_light_onPrimaryContainer,
    secondary = serenity_light_secondary,
    onSecondary = serenity_light_onSecondary,
    secondaryContainer = serenity_light_secondaryContainer,
    onSecondaryContainer = serenity_light_onSecondaryContainer,
    tertiary = serenity_light_tertiary,
    onTertiary = serenity_light_onTertiary,
    tertiaryContainer = serenity_light_tertiaryContainer,
    onTertiaryContainer = serenity_light_onTertiaryContainer,
    error = serenity_light_error,
    errorContainer = serenity_light_errorContainer,
    onError = serenity_light_onError,
    onErrorContainer = serenity_light_onErrorContainer,
    background = serenity_light_background,
    onBackground = serenity_light_onBackground,
    surface = serenity_light_surface,
    onSurface = serenity_light_onSurface,
    surfaceVariant = serenity_light_surfaceVariant,
    onSurfaceVariant = serenity_light_onSurfaceVariant,
    outline = serenity_light_outline,
    inverseOnSurface = serenity_light_inverseOnSurface,
    inverseSurface = serenity_light_inverseSurface,
    inversePrimary = serenity_light_inversePrimary,
    surfaceTint = serenity_light_surfaceTint,
    outlineVariant = serenity_light_outlineVariant,
    scrim = serenity_light_scrim,
)

private val SerenityDarkColorScheme = darkColorScheme(
    primary = serenity_dark_primary,
    onPrimary = serenity_dark_onPrimary,
    primaryContainer = serenity_dark_primaryContainer,
    onPrimaryContainer = serenity_dark_onPrimaryContainer,
    secondary = serenity_dark_secondary,
    onSecondary = serenity_dark_onSecondary,
    secondaryContainer = serenity_dark_secondaryContainer,
    onSecondaryContainer = serenity_dark_onSecondaryContainer,
    tertiary = serenity_dark_tertiary,
    onTertiary = serenity_dark_onTertiary,
    tertiaryContainer = serenity_dark_tertiaryContainer,
    onTertiaryContainer = serenity_dark_onTertiaryContainer,
    error = serenity_dark_error,
    errorContainer = serenity_dark_errorContainer,
    onError = serenity_dark_onError,
    onErrorContainer = serenity_dark_onErrorContainer,
    background = serenity_dark_background,
    onBackground = serenity_dark_onBackground,
    surface = serenity_dark_surface,
    onSurface = serenity_dark_onSurface,
    surfaceVariant = serenity_dark_surfaceVariant,
    onSurfaceVariant = serenity_dark_onSurfaceVariant,
    outline = serenity_dark_outline,
    inverseOnSurface = serenity_dark_inverseOnSurface,
    inverseSurface = serenity_dark_inverseSurface,
    inversePrimary = serenity_dark_inversePrimary,
    surfaceTint = serenity_dark_surfaceTint,
    outlineVariant = serenity_dark_outlineVariant,
    scrim = serenity_dark_scrim,
)

// Sunset Color Schemes
private val SunsetLightColorScheme = lightColorScheme(
    primary = sunset_light_primary,
    onPrimary = sunset_light_onPrimary,
    primaryContainer = sunset_light_primaryContainer,
    onPrimaryContainer = sunset_light_onPrimaryContainer,
    secondary = sunset_light_secondary,
    onSecondary = sunset_light_onSecondary,
    secondaryContainer = sunset_light_secondaryContainer,
    onSecondaryContainer = sunset_light_onSecondaryContainer,
    tertiary = sunset_light_tertiary,
    onTertiary = sunset_light_onTertiary,
    tertiaryContainer = sunset_light_tertiaryContainer,
    onTertiaryContainer = sunset_light_onTertiaryContainer,
    error = sunset_light_error,
    errorContainer = sunset_light_errorContainer,
    onError = sunset_light_onError,
    onErrorContainer = sunset_light_onErrorContainer,
    background = sunset_light_background,
    onBackground = sunset_light_onBackground,
    surface = sunset_light_surface,
    onSurface = sunset_light_onSurface,
    surfaceVariant = sunset_light_surfaceVariant,
    onSurfaceVariant = sunset_light_onSurfaceVariant,
    outline = sunset_light_outline,
    inverseOnSurface = sunset_light_inverseOnSurface,
    inverseSurface = sunset_light_inverseSurface,
    inversePrimary = sunset_light_inversePrimary,
    surfaceTint = sunset_light_surfaceTint,
    outlineVariant = sunset_light_outlineVariant,
    scrim = sunset_light_scrim,
)

private val SunsetDarkColorScheme = darkColorScheme(
    primary = sunset_dark_primary,
    onPrimary = sunset_dark_onPrimary,
    primaryContainer = sunset_dark_primaryContainer,
    onPrimaryContainer = sunset_dark_onPrimaryContainer,
    secondary = sunset_dark_secondary,
    onSecondary = sunset_dark_onSecondary,
    secondaryContainer = sunset_dark_secondaryContainer,
    onSecondaryContainer = sunset_dark_onSecondaryContainer,
    tertiary = sunset_dark_tertiary,
    onTertiary = sunset_dark_onTertiary,
    tertiaryContainer = sunset_dark_tertiaryContainer,
    onTertiaryContainer = sunset_dark_onTertiaryContainer,
    error = sunset_dark_error,
    errorContainer = sunset_dark_errorContainer,
    onError = sunset_dark_onError,
    onErrorContainer = sunset_dark_onErrorContainer,
    background = sunset_dark_background,
    onBackground = sunset_dark_onBackground,
    surface = sunset_dark_surface,
    onSurface = sunset_dark_onSurface,
    surfaceVariant = sunset_dark_surfaceVariant,
    onSurfaceVariant = sunset_dark_onSurfaceVariant,
    outline = sunset_dark_outline,
    inverseOnSurface = sunset_dark_inverseOnSurface,
    inverseSurface = sunset_dark_inverseSurface,
    inversePrimary = sunset_dark_inversePrimary,
    surfaceTint = sunset_dark_surfaceTint,
    outlineVariant = sunset_dark_outlineVariant,
    scrim = sunset_dark_scrim,
)

// Forest Color Schemes
private val ForestLightColorScheme = lightColorScheme(
    primary = forest_light_primary,
    onPrimary = forest_light_onPrimary,
    primaryContainer = forest_light_primaryContainer,
    onPrimaryContainer = forest_light_onPrimaryContainer,
    secondary = forest_light_secondary,
    onSecondary = forest_light_onSecondary,
    secondaryContainer = forest_light_secondaryContainer,
    onSecondaryContainer = forest_light_onSecondaryContainer,
    tertiary = forest_light_tertiary,
    onTertiary = forest_light_onTertiary,
    tertiaryContainer = forest_light_tertiaryContainer,
    onTertiaryContainer = forest_light_onTertiaryContainer,
    error = forest_light_error,
    errorContainer = forest_light_errorContainer,
    onError = forest_light_onError,
    onErrorContainer = forest_light_onErrorContainer,
    background = forest_light_background,
    onBackground = forest_light_onBackground,
    surface = forest_light_surface,
    onSurface = forest_light_onSurface,
    surfaceVariant = forest_light_surfaceVariant,
    onSurfaceVariant = forest_light_onSurfaceVariant,
    outline = forest_light_outline,
    inverseOnSurface = forest_light_inverseOnSurface,
    inverseSurface = forest_light_inverseSurface,
    inversePrimary = forest_light_inversePrimary,
    surfaceTint = forest_light_surfaceTint,
    outlineVariant = forest_light_outlineVariant,
    scrim = forest_light_scrim,
)

private val ForestDarkColorScheme = darkColorScheme(
    primary = forest_dark_primary,
    onPrimary = forest_dark_onPrimary,
    primaryContainer = forest_dark_primaryContainer,
    onPrimaryContainer = forest_dark_onPrimaryContainer,
    secondary = forest_dark_secondary,
    onSecondary = forest_dark_onSecondary,
    secondaryContainer = forest_dark_secondaryContainer,
    onSecondaryContainer = forest_dark_onSecondaryContainer,
    tertiary = forest_dark_tertiary,
    onTertiary = forest_dark_onTertiary,
    tertiaryContainer = forest_dark_tertiaryContainer,
    onTertiaryContainer = forest_dark_onTertiaryContainer,
    error = forest_dark_error,
    errorContainer = forest_dark_errorContainer,
    onError = forest_dark_onError,
    onErrorContainer = forest_dark_onErrorContainer,
    background = forest_dark_background,
    onBackground = forest_dark_onBackground,
    surface = forest_dark_surface,
    onSurface = forest_dark_onSurface,
    surfaceVariant = forest_dark_surfaceVariant,
    onSurfaceVariant = forest_dark_onSurfaceVariant,
    outline = forest_dark_outline,
    inverseOnSurface = forest_dark_inverseOnSurface,
    inverseSurface = forest_dark_inverseSurface,
    inversePrimary = forest_dark_inversePrimary,
    surfaceTint = forest_dark_surfaceTint,
    outlineVariant = forest_dark_outlineVariant,
    scrim = forest_dark_scrim,
)

// Ocean Color Schemes
private val OceanLightColorScheme = lightColorScheme(
    primary = ocean_light_primary,
    onPrimary = ocean_light_onPrimary,
    primaryContainer = ocean_light_primaryContainer,
    onPrimaryContainer = ocean_light_onPrimaryContainer,
    secondary = ocean_light_secondary,
    onSecondary = ocean_light_onSecondary,
    secondaryContainer = ocean_light_secondaryContainer,
    onSecondaryContainer = ocean_light_onSecondaryContainer,
    tertiary = ocean_light_tertiary,
    onTertiary = ocean_light_onTertiary,
    tertiaryContainer = ocean_light_tertiaryContainer,
    onTertiaryContainer = ocean_light_onTertiaryContainer,
    error = ocean_light_error,
    errorContainer = ocean_light_errorContainer,
    onError = ocean_light_onError,
    onErrorContainer = ocean_light_onErrorContainer,
    background = ocean_light_background,
    onBackground = ocean_light_onBackground,
    surface = ocean_light_surface,
    onSurface = ocean_light_onSurface,
    surfaceVariant = ocean_light_surfaceVariant,
    onSurfaceVariant = ocean_light_onSurfaceVariant,
    outline = ocean_light_outline,
    inverseOnSurface = ocean_light_inverseOnSurface,
    inverseSurface = ocean_light_inverseSurface,
    inversePrimary = ocean_light_inversePrimary,
    surfaceTint = ocean_light_surfaceTint,
    outlineVariant = ocean_light_outlineVariant,
    scrim = ocean_light_scrim,
)

private val OceanDarkColorScheme = darkColorScheme(
    primary = ocean_dark_primary,
    onPrimary = ocean_dark_onPrimary,
    primaryContainer = ocean_dark_primaryContainer,
    onPrimaryContainer = ocean_dark_onPrimaryContainer,
    secondary = ocean_dark_secondary,
    onSecondary = ocean_dark_onSecondary,
    secondaryContainer = ocean_dark_secondaryContainer,
    onSecondaryContainer = ocean_dark_onSecondaryContainer,
    tertiary = ocean_dark_tertiary,
    onTertiary = ocean_dark_onTertiary,
    tertiaryContainer = ocean_dark_tertiaryContainer,
    onTertiaryContainer = ocean_dark_onTertiaryContainer,
    error = ocean_dark_error,
    errorContainer = ocean_dark_errorContainer,
    onError = ocean_dark_onError,
    onErrorContainer = ocean_dark_onErrorContainer,
    background = ocean_dark_background,
    onBackground = ocean_dark_onBackground,
    surface = ocean_dark_surface,
    onSurface = ocean_dark_onSurface,
    surfaceVariant = ocean_dark_surfaceVariant,
    onSurfaceVariant = ocean_dark_onSurfaceVariant,
    outline = ocean_dark_outline,
    inverseOnSurface = ocean_dark_inverseOnSurface,
    inverseSurface = ocean_dark_inverseSurface,
    inversePrimary = ocean_dark_inversePrimary,
    surfaceTint = ocean_dark_surfaceTint,
    outlineVariant = ocean_dark_outlineVariant,
    scrim = ocean_dark_scrim,
)

@Composable
fun MoodTrackerTheme(
    appTheme: AppTheme = AppTheme.SERENITY,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled by default to use custom themes
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            when (appTheme) {
                AppTheme.SERENITY -> if (darkTheme) SerenityDarkColorScheme else SerenityLightColorScheme
                AppTheme.SUNSET -> if (darkTheme) SunsetDarkColorScheme else SunsetLightColorScheme
                AppTheme.FOREST -> if (darkTheme) ForestDarkColorScheme else ForestLightColorScheme
                AppTheme.OCEAN -> if (darkTheme) OceanDarkColorScheme else OceanLightColorScheme
            }
        }
    }

    CompositionLocalProvider(LocalAppTheme provides appTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
