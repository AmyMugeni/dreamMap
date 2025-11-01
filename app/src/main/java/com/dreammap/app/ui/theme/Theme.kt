package com.dreammap.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// --- Light Color Scheme (Default) ---
private val LightColorScheme = lightColorScheme(
    primary = BrightBlue,          // Main elements (buttons, primary text)
    secondary = NavyBlue,          // Deeper contrast elements (e.g., secondary buttons)
    tertiary = LightCyan,          // Accents and highlights

    background = White,            // General screen background
    surface = LightGray,           // Card backgrounds, dialogs
    error = Color(0xFFB00020),     // Error states
    onPrimary = White,             // Text on primary color
    onBackground = NavyBlue        // Text on screen background
    // ... other colors
)

// --- Dark Color Scheme (Optional, but good practice) ---
private val DarkColorScheme = darkColorScheme(
    primary = LightCyan,           // Main elements (buttons, primary text)
    secondary = BrightBlue,        // Contrast elements
    tertiary = IdeaYellow,         // Accents

    background = NavyBlue,         // General screen background
    surface = DarkNavy,   // Card backgrounds (darker navy)
    error = Color(0xFFCF6679),
    onPrimary = NavyBlue,          // Text on primary color
    onBackground = White           // Text on screen background
    // ... other colors
)

// --- DreamMapTheme Composable ---

@Composable
fun DreamMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // Match background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Make sure Typography.kt exists
        content = content
    )
}