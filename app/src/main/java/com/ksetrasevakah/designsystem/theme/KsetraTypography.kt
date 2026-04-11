package com.ksetrasevakah.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Semantic font family aliases. Currently mapped to the platform default family; swap to
 * `FontFamily(Font(R.font....))` when bundling DM Sans, Playfair Display, or JetBrains Mono.
 */
val DmSansFamily = FontFamily.Default
val PlayfairDisplayFamily = FontFamily.Default
val JetBrainsMonoFamily = FontFamily.Default

/** Unspecified lets `LocalContentColor` / theme roles control text on buttons and surfaces. */
private val inherit = Color.Unspecified

val KsetraTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        color = KsetraTextPrimary
    ),
    headlineMedium = TextStyle(
        fontFamily = PlayfairDisplayFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        color = KsetraTextPrimary
    ),
    headlineSmall = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        color = KsetraTextPrimary
    ),
    titleLarge = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        color = KsetraTextPrimary
    ),
    titleMedium = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        color = KsetraTextPrimary
    ),
    bodyLarge = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 24.sp,
        color = inherit
    ),
    bodyMedium = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        color = inherit
    ),
    bodySmall = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        color = inherit
    ),
    labelLarge = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
        color = inherit,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 16.sp,
        color = inherit,
        letterSpacing = 0.25.sp
    ),
    labelSmall = TextStyle(
        fontFamily = DmSansFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        color = inherit,
        letterSpacing = 0.2.sp
    )
)

/** Monospace style used for data readouts (currents, timers, voltages) */
val KsetraMonoStyle = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    color = KsetraAccentGreen
)
