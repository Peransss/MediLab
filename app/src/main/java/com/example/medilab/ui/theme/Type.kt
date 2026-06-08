package com.example.medilab.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Healthcare typography — Figtree (heading) + Noto Sans (body)
// System FontFamily.Default used as fallback; replace with:
//   FontFamily(Font(R.font.figtree_bold, FontWeight.Bold)) for heading
//   FontFamily(Font(R.font.noto_sans_regular, FontWeight.Normal)) for body
private val HeadingFont = FontFamily.Default
private val BodyFont = FontFamily.Default

val MediLabTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = HeadingFont, fontWeight = FontWeight.Bold, fontSize = 36.sp, lineHeight = 44.sp
    ),
    displayMedium = TextStyle(
        fontFamily = HeadingFont, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 36.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = HeadingFont, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 32.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = HeadingFont, fontWeight = FontWeight.SemiBold, fontSize = 20.sp, lineHeight = 28.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = HeadingFont, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, lineHeight = 24.sp
    ),
    titleLarge = TextStyle(
        fontFamily = HeadingFont, fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = BodyFont, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp
    )
)
