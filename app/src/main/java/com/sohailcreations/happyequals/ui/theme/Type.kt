
package com.sohailcreations.happyequals.ui.theme

import androidx.compose.material3.Typography as MaterialTypography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.sohailcreations.happyequals.R

// ==========================================
// HAPPY EQUALS - FONT FAMILIES
// ==========================================

// Branding, headings and screen titles
val JakartaFamily = FontFamily(
    Font(
        R.font.plus_jakarta_sans_semibold,
        FontWeight.SemiBold
    ),
    Font(
        R.font.plus_jakarta_sans_bold,
        FontWeight.Bold
    )
)

// Calculator numbers, buttons and body text
val ManropeFamily = FontFamily(
    Font(
        R.font.manrope_regular,
        FontWeight.Normal
    ),
    Font(
        R.font.manrope_medium,
        FontWeight.Medium
    ),
    Font(
        R.font.manrope_bold,
        FontWeight.Bold
    )
)

// ==========================================
// HAPPY EQUALS - TYPOGRAPHY SYSTEM
// ==========================================

val HappyTypography = MaterialTypography(

    // Large calculator result
    displayLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 48.sp,
        lineHeight = 58.sp,
        letterSpacing = (-1.5).sp
    ),

    // Standard calculator result
    displayMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 38.sp,
        lineHeight = 48.sp,
        letterSpacing = (-1).sp
    ),

    // Main screen headings
    headlineLarge = TextStyle(
        fontFamily = JakartaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 39.sp
    ),

    headlineMedium = TextStyle(
        fontFamily = JakartaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        lineHeight = 33.sp
    ),

    // App name and card headings
    titleLarge = TextStyle(
        fontFamily = JakartaFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 21.sp,
        lineHeight = 29.sp
    ),

    titleMedium = TextStyle(
        fontFamily = JakartaFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        lineHeight = 24.sp
    ),

    // Main body text
    bodyLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 25.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp
    ),

    bodySmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 19.sp
    ),

    // Calculator buttons
    labelLarge = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 23.sp
    ),

    // Navigation and secondary buttons
    labelMedium = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),

    // Small captions and subtitles
    labelSmall = TextStyle(
        fontFamily = ManropeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)

// Compatibility with the default Android Studio theme
val Typography = HappyTypography