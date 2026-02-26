package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography =
    Typography(
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        headlineMedium =
            TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
            ),
        titleMedium =
            TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText,
            ),
        bodySmall =
            TextStyle(
                fontSize = 14.sp,
                color = SecondaryText,
            ),
    )
