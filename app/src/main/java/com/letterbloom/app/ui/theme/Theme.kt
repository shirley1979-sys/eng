package com.letterbloom.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val LetterBloomColorScheme = darkColorScheme(
    primary = AmberGold,
    onPrimary = NavyDeep,
    primaryContainer = AmberPale,
    onPrimaryContainer = AmberLight,
    secondary = TealElectric,
    onSecondary = NavyDeep,
    secondaryContainer = TealPale,
    onSecondaryContainer = TealLight,
    tertiary = CoralAccent,
    onTertiary = OffWhite,
    background = NavyDeep,
    onBackground = OffWhite,
    surface = NavyMid,
    onSurface = OffWhite,
    surfaceVariant = NavySurface,
    onSurfaceVariant = White80,
    outline = BorderColor,
    outlineVariant = NavyLight,
)

@Composable
fun LetterBloomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LetterBloomColorScheme,
        typography = Typography,
        content = content
    )
}
