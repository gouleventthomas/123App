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
    primary = BrandTealDeep,
    onPrimary = Color.White,
    primaryContainer = BrandTeal,
    onPrimaryContainer = Color.White,
    secondary = BrandCyan,
    onSecondary = Color.White,
    secondaryContainer = BrandCyan,
    onSecondaryContainer = Color(0xFF06363F),
    background = SurfaceLight,
    onBackground = Color(0xFF18201F),
    surface = Color.White,
    onSurface = Color(0xFF18201F),
    surfaceVariant = Color(0xFFDCEEF0),
    onSurfaceVariant = Color(0xFF3F484A),
)

private val DarkColors = darkColorScheme(
    primary = BrandTeal,
    onPrimary = Color(0xFF06302D),
    primaryContainer = BrandTealDeep,
    onPrimaryContainer = Color.White,
    secondary = BrandCyan,
    onSecondary = Color(0xFF06363F),
    background = SurfaceDark,
    onBackground = Color(0xFFDFE3E3),
    surface = Color(0xFF161E20),
    onSurface = Color(0xFFDFE3E3),
    surfaceVariant = Color(0xFF2A3537),
    onSurfaceVariant = Color(0xFFBFC8C9),
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
            window.statusBarColor = BrandCyan.toArgb()
            // Cyan clair => icônes système sombres pour rester lisibles.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
