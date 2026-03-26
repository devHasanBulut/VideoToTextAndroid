// Theme.kt
package com.duhan.videototext.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    secondary = PrimaryOrangeDark,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onPrimary = Color.White,
    onBackground = TextBlack,
    onSurface = TextBlack,
    onSurfaceVariant = TextGray
)

// We focus on Light Theme design primarily.
private val DarkColorScheme = lightColorScheme( // Reuse light scheme or define specific dark mappings if needed. For now sticking to design.
    primary = PrimaryOrange,
    secondary = PrimaryOrangeDark,
    background = BackgroundLight,
    surface = SurfaceWhite,
    onPrimary = Color.White,
    onBackground = TextBlack,
    onSurface = TextBlack,
    onSurfaceVariant = TextGray
)

@Composable
fun VideoToTextTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Design is mainly Light Theme based
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            // Status bar icons should be dark for light background
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true 
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}