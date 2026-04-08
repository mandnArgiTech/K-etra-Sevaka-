package com.ksetrasevakah.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val KsetraDarkColorScheme = darkColorScheme(
    primary = KsetraAccentGreen,
    onPrimary = KsetraDarkBackground,
    primaryContainer = KsetraAccentGreenDim,
    onPrimaryContainer = KsetraTextPrimary,
    secondary = KsetraPurple,
    onSecondary = KsetraDarkBackground,
    secondaryContainer = KsetraPurpleDim,
    onSecondaryContainer = KsetraTextPrimary,
    tertiary = KsetraAmber,
    onTertiary = KsetraDarkBackground,
    tertiaryContainer = KsetraAmberDim,
    onTertiaryContainer = KsetraTextPrimary,
    error = KsetraRed,
    onError = KsetraTextPrimary,
    errorContainer = KsetraRedDim,
    onErrorContainer = KsetraTextPrimary,
    background = KsetraDarkBackground,
    onBackground = KsetraTextPrimary,
    surface = KsetraSurface,
    onSurface = KsetraTextPrimary,
    surfaceVariant = KsetraCard,
    onSurfaceVariant = KsetraTextSecondary,
    outline = KsetraBorder,
    outlineVariant = KsetraBorderLight,
)

/**
 * Kṣetra Sevakaḥ dark agricultural theme wrapping Material 3.
 */
@Composable
fun KsetraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = KsetraDarkColorScheme,
        typography = KsetraTypography,
        content = content
    )
}
