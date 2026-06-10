package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ElectricViolet,
    secondary = MintTeal,
    tertiary = WarmGold,
    background = BaseBg,
    surface = CardBg,
    onPrimary = Slate100,
    onSecondary = BaseBg,
    onTertiary = BaseBg,
    onBackground = Slate100,
    onSurface = Slate100,
    surfaceVariant = CardBgGlass,
    onSurfaceVariant = Slate300
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
