package com.letterbloom.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LetterBloomColorScheme = lightColorScheme(
    primary              = TiffanyMint,
    onPrimary            = PureWhite,
    primaryContainer     = TiffanyCream,
    onPrimaryContainer   = TextDark,
    secondary            = TiffanyLight,
    onSecondary          = PureWhite,
    secondaryContainer   = IceMint,
    onSecondaryContainer = TextPrimary,
    tertiary             = TiffanyDeep,
    onTertiary           = PureWhite,
    background           = SoftWhite,
    onBackground         = TextDark,
    surface              = PureWhite,
    onSurface            = TextDark,
    surfaceVariant       = MintWhite,
    onSurfaceVariant     = TextMedium,
    outline              = BorderMint,
    outlineVariant       = BorderSoft,
)

@Composable
fun LetterBloomTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LetterBloomColorScheme,
        typography = Typography,
        content = content
    )
}
