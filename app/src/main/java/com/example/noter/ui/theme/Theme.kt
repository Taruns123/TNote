package com.example.noter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Pine,
    onPrimary = Color.White,
    primaryContainer = PineContainer,
    onPrimaryContainer = OnPineContainer,
    secondary = InkSoft,
    onSecondary = Color.White,
    secondaryContainer = PaperSunken,
    onSecondaryContainer = Ink,
    tertiary = Ember,
    onTertiary = Color.White,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperSunken,
    onSurfaceVariant = InkSoft,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = PaperRaised,
    surfaceContainer = PaperRaised,
    surfaceContainerHigh = PaperSunken,
    surfaceContainerHighest = PaperSunken,
    outline = InkFaint,
    outlineVariant = Hairline,
    inverseSurface = Ink,
    inverseOnSurface = Paper,
    inversePrimary = PineLight,
)

private val DarkColors = darkColorScheme(
    primary = PineLight,
    onPrimary = Color(0xFF0B2A20),
    primaryContainer = PineDarkContainer,
    onPrimaryContainer = OnPineDarkContainer,
    secondary = ChalkSoft,
    onSecondary = Night,
    secondaryContainer = NightSunken,
    onSecondaryContainer = Chalk,
    tertiary = EmberLight,
    onTertiary = Night,
    background = Night,
    onBackground = Chalk,
    surface = Night,
    onSurface = Chalk,
    surfaceVariant = NightSunken,
    onSurfaceVariant = ChalkSoft,
    surfaceContainerLowest = Color(0xFF0F0F0D),
    surfaceContainerLow = NightRaised,
    surfaceContainer = NightRaised,
    surfaceContainerHigh = NightSunken,
    surfaceContainerHighest = NightSunken,
    outline = ChalkFaint,
    outlineVariant = NightHairline,
    inverseSurface = Chalk,
    inverseOnSurface = Night,
    inversePrimary = Pine,
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun NoterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = AppShapes,
        content = content
    )
}
