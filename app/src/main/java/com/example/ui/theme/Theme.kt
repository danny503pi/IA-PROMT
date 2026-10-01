package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaLight,
    onPrimary = Color(0xFF451400),
    primaryContainer = TerracottaDark,
    onPrimaryContainer = Color(0xFFFFDBCF),
    secondary = JadeLight,
    onSecondary = Color(0xFF003831),
    secondaryContainer = JadeDark,
    onSecondaryContainer = Color(0xFF70F7E5),
    tertiary = GoldLight,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = GoldDark,
    onTertiaryContainer = Color(0xFFFFDF9E),
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = CardBorderDark,
    error = ErrorCrimson,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCF),
    onPrimaryContainer = TerracottaDark,
    secondary = JadeSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA7F0E5),
    onSecondaryContainer = JadeDark,
    tertiary = GoldDark,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF452B00),
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = CardBorderLight,
    error = ErrorCrimson,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep distinct cultural palette by default
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
