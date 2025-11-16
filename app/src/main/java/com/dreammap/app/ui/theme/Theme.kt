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
    primary = DarkPurple,          // Main elements (buttons, primary text)
    secondary = MediumPurple,      // Secondary buttons, elements
    tertiary = LightPurple,        // Accents and highlights

    background = VeryLightPurple,  // General screen background (lavender tint)
    surface = White,               // Card backgrounds, dialogs
    surfaceVariant = LightPurple.copy(alpha = 0.3f), // Subtle purple tint for variant surfaces
    error = Color(0xFFB00020),     // Error states
    onPrimary = White,             // Text on primary color
    onSecondary = White,           // Text on secondary color
    onBackground = DarkPurple,     // Text on screen background
    onSurface = DarkPurple        // Text on surface
)

// --- Dark Color Scheme (Purple & Black Theme) ---
private val DarkColorScheme = darkColorScheme(
    primary = MediumPurple,        // Main elements (buttons, primary text) - vibrant purple
    secondary = LightPurple,       // Secondary elements - lighter purple
    tertiary = Gold,               // Accents (gold for gamification)

    background = NearBlack,        // General screen background (near black)
    surface = DarkBlack,           // Card backgrounds (dark black)
    surfaceVariant = Charcoal,    // Elevated surfaces (charcoal)
    error = Color(0xFFCF6679),
    onPrimary = White,             // Text on primary color
    onSecondary = White,           // Text on secondary color
    onBackground = White,          // Text on screen background
    onSurface = White,            // Text on surface
    onSurfaceVariant = LightPurple.copy(alpha = 0.8f) // Text on variant surfaces
)

// --- DreamMapTheme Composable ---

@Composable
fun DreamMapTheme(
    darkTheme: Boolean = true, // Default to dark theme (purple & black)
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disable dynamic color to use our custom purple & black theme
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