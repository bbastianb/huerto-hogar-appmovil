// ui/theme/Theme.kt
package com.abs.huerto_hogar_appmovil.ui.theme

import android.app.Activity
import androidx.compose.ui.graphics.Color
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E8B57), // Verde Esmeralda
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFA8D5BA),
    onPrimaryContainer = Color(0xFF00210E),

    secondary =Color(0xFFFFD700), // Amarillo Mostaza
    onSecondary =Color(0xFF000000),
    secondaryContainer =Color(0xFFFFF0A8),
    onSecondaryContainer =Color(0xFF241A00),

    tertiary =Color(0xFF8B4513), // Marrón Claro
    onTertiary =Color(0xFFFFFFFF),
    tertiaryContainer =Color(0xFFFFDCC2),
    onTertiaryContainer =Color(0xFF301400),

    background =Color(0xFFF7F7F7), // Blanco Suave
    onBackground =Color(0xFF333333), // Gris Oscuro
    surface =Color(0xFFFFFFFF),
    onSurface =Color(0xFF333333),

    surfaceVariant =Color(0xFFDEE5D9),
    onSurfaceVariant =Color(0xFF424941),

    outline =Color(0xFF727970),
    outlineVariant =Color(0xFFC1C9BE)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),        // Verde más claro para dark mode
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color(0xFFFFFFFF),

    secondary = Color(0xFFFFC107),      // Amarillo para dark mode
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFF57C00),
    onSecondaryContainer = Color(0xFFFFFFFF),

    background = Color(0xFF121212),     // Fondo oscuro
    onBackground = Color(0xFFFFFFFF),   // Texto blanco

    surface = Color(0xFF1E1E1E),        // Cards oscuros
    onSurface = Color(0xFFFFFFFF)
)
@Composable
fun HuertohogarappmovilTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,      // Desactiva dynamic colors por ahora
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}