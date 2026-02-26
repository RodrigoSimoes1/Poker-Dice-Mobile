package com.example.myapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val darkColorScheme =
    darkColorScheme(
    /*primary = PrimaryText,
    secondary = SecondaryText,
    tertiary = StatIconMoney,
    background = Background,
    onPrimary = Background,
    onSecondary = Background,
    onTertiary = Background,
    onBackground = PrimaryText,
    onSurface = PrimaryText*/
    )

private val lightColorScheme =
    lightColorScheme(
    /*primary = PrimaryText,
    secondary = SecondaryText,
    tertiary = StatIconMoney,
    background = Background,
    onPrimary = Background,
    onSecondary = Background,
    onTertiary = Background,
    onBackground = PrimaryText,
    onSurface = PrimaryText*/
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> darkColorScheme
            else -> lightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
