package com.sohailcreations.happyequals.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HappyEqualsLightColors = lightColorScheme(
    primary = AccentBlue,
    onPrimary = AppSurface,

    secondary = AccentTeal,
    onSecondary = AppSurface,

    tertiary = AccentPurple,
    onTertiary = AppSurface,

    background = AppBackground,
    onBackground = TextPrimary,

    surface = AppSurface,
    onSurface = TextPrimary,

    surfaceVariant = AppSurfaceSoft,
    onSurfaceVariant = TextSecondary,

    outline = AppBorder,
    error = ErrorColor,
    onError = AppSurface
)

@Composable
fun HappyEqualsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HappyEqualsLightColors,
        typography = HappyTypography,
        content = content
    )
}