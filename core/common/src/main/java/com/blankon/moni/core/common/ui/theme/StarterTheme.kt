package com.blankon.moni.core.common.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

//private val DarkColorScheme = darkColorScheme(
//    primary = Purple80,
//    secondary = PurpleGrey80,
//    tertiary = Pink80,
//    background = BackgroundDark,
//    surface = SurfaceDark
//)
//
//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40,
//    background = BackgroundLight,
//    surface = SurfaceLight
//)
//


private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,

    secondary = Color(0xFFB5D77C),
    onSecondary = Color(0xFF1A2E05),

    tertiary = Color(0xFFB5C99A),
    onTertiary = Color(0xFF202A16),

    background = BackgroundDark,
    onBackground = OnBackgroundDark,

    surface = SurfaceDark,
    onSurface = OnSurfaceDark,

    surfaceVariant = Color(0xFF30382A),
    onSurfaceVariant = Color(0xFFD0D9C5),

    outline = BorderDark,
    error = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,

    secondary = PrimaryDark,
    onSecondary = Color.White,

    tertiary = Color(0xFF8BAE52),
    onTertiary = Color(0xFF172600),

    background = BackgroundLight,
    onBackground = OnBackgroundLight,

    surface = SurfaceLight,
    onSurface = OnSurfaceLight,

    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = TextSecondary,

    outline = BorderLight,
    error = Error
)

object AppTheme {
    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current
}

@Composable
fun StarterTheme(
    typography: Typography = AppTheme.typography,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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

    CompositionLocalProvider(LocalTypography provides typography)  {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}