package com.btkboz.reeliov3.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ReelioDarkPrimary,
    onPrimary = ReelioDarkPrimaryForeground,
    secondary = ReelioDarkSecondary,
    onSecondary = ReelioDarkSecondaryForeground,
    background = ReelioDarkBackground,
    onBackground = ReelioDarkForeground,
    surface = ReelioDarkCard,
    onSurface = ReelioDarkForeground,
    surfaceVariant = ReelioDarkMuted,
    onSurfaceVariant = ReelioDarkMutedForeground,
    outline = ReelioDarkBorder,
    outlineVariant = ReelioDarkRing,
    error = ReelioDarkDestructive,
)

private val LightColorScheme = lightColorScheme(
    primary = ReelioLightPrimary,
    onPrimary = ReelioLightPrimaryForeground,
    secondary = ReelioLightSecondary,
    onSecondary = ReelioLightSecondaryForeground,
    background = ReelioLightBackground,
    onBackground = ReelioLightForeground,
    surface = ReelioLightCard,
    onSurface = ReelioLightForeground,
    surfaceVariant = ReelioLightMuted,
    onSurfaceVariant = ReelioLightMutedForeground,
    outline = ReelioLightBorder,
    outlineVariant = ReelioLightRing,
    error = ReelioLightDestructive,
)

enum class ReelioThemeMode { System, Light, Dark }

@Composable
fun ReelioV3Theme(
    mode: ReelioThemeMode = ReelioThemeMode.System,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (mode) {
        ReelioThemeMode.System -> isSystemInDarkTheme()
        ReelioThemeMode.Light -> false
        ReelioThemeMode.Dark -> true
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
