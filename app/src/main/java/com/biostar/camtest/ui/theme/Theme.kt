package com.biostar.camtest.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Gray900,
    onPrimary = White,
    primaryContainer = Gray100,
    onPrimaryContainer = Gray900,
    
    secondary = Gray700,
    onSecondary = White,
    secondaryContainer = Gray200,
    onSecondaryContainer = Gray800,
    
    tertiary = AccentBlue,
    onTertiary = White,
    tertiaryContainer = AccentBlue.copy(alpha = 0.1f),
    onTertiaryContainer = AccentBlue,
    
    background = White,
    onBackground = Gray900,
    
    surface = White,
    onSurface = Gray900,
    surfaceVariant = Gray50,
    onSurfaceVariant = Gray600,
    
    outline = Gray300,
    outlineVariant = Gray200,
    
    error = Error,
    onError = White,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,
    
    inverseSurface = Gray900,
    inverseOnSurface = White,
    inversePrimary = Gray100,
    
    scrim = Black.copy(alpha = 0.4f),
    surfaceTint = Gray900
)

@Composable
fun USBCamTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
