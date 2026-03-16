package com.example.europos_scanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = FeriaPrimary,
    onPrimary = FeriaOnPrimary,
    secondary = FeriaSecondary,
    onSecondary = FeriaOnSecondary,
    tertiary = FeriaAccent,
    background = FeriaBackground,
    onBackground = FeriaOnBackground,
    surface = FeriaBackground,
    onSurface = FeriaOnSurface,
    error = FeriaError,
    onError = FeriaOnError
)

@Composable
fun EuroposScannerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = EuroposScannerTypography,
        content = content
    )
}
