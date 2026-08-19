package com.example.aapatmitra.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CrimsonRed = Color(0xFFDC2626)
val CrimsonDark = Color(0xFF991B1B)
val CrimsonGlow = Color(0xFFEF4444)
val CrimsonRedGlow = Color(0xFFEF4444)
val CrimsonSubtle = Color(0xFFFEE2E2)
val EmergencyDarkBg = Color(0xFF0B0F19)
val CardDarkBg = Color(0xFF161E2E)
val CardSurfaceBorder = Color(0xFF283548)
val AccentAmber = Color(0xFFF59E0B)
val AmberWarning = Color(0xFFF59E0B)
val AccentEmerald = Color(0xFF10B981)
val EmeraldGreen = Color(0xFF10B981)
val AccentCyan = Color(0xFF06B6D4)
val SlateGray = Color(0xFF94A3B8)

private val DarkColorScheme = darkColorScheme(
    primary = CrimsonRed,
    onPrimary = Color.White,
    primaryContainer = CrimsonDark,
    onPrimaryContainer = Color.White,
    secondary = AccentCyan,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0E3846),
    onSecondaryContainer = Color(0xFFA5F3FC),
    tertiary = AccentAmber,
    onTertiary = Color.Black,
    background = EmergencyDarkBg,
    onBackground = Color(0xFFF8FAFC),
    surface = CardDarkBg,
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = CardSurfaceBorder,
    error = Color(0xFFEF4444),
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CrimsonRed,
    onPrimary = Color.White,
    primaryContainer = CrimsonSubtle,
    onPrimaryContainer = CrimsonDark,
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFFD97706),
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626),
    onError = Color.White
)

@Composable
fun AapatMitraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
