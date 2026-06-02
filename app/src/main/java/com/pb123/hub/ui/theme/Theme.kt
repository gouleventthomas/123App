package com.pb123.hub.ui.theme

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

private val LightColors = lightColorScheme(
    primary = PBBlue,
    onPrimary = Color.White,
    primaryContainer = PBBlueLight,
    onPrimaryContainer = Color.White,
    secondary = PBRed,
    onSecondary = Color.White,
    background = SurfaceLight,
    onBackground = Color(0xFF1A1C1E),
    surface = Color.White,
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE3E8EF),
    onSurfaceVariant = Color(0xFF42474E),
)

private val DarkColors = darkColorScheme(
    primary = PBBlueLight,
    onPrimary = Color.White,
    primaryContainer = PBBlueDark,
    onPrimaryContainer = Color.White,
    secondary = PBRed,
    onSecondary = Color.White,
    background = SurfaceDark,
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1A1F24),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF2A3038),
    onSurfaceVariant = Color(0xFFC2C7CF),
)

@Composable
fun PB123HubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PBBlue.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
