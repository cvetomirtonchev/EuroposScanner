package bg.europos_scanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = FeriaPrimary,
    onPrimary = FeriaOnPrimary,
    primaryContainer = FeriaAccent,
    onPrimaryContainer = FeriaTextPrimary,
    secondary = FeriaSecondary,
    onSecondary = FeriaOnSecondary,
    secondaryContainer = FeriaSurfaceVariant,
    onSecondaryContainer = FeriaTextPrimary,
    tertiary = FeriaAccent,
    background = FeriaBackground,
    onBackground = FeriaOnBackground,
    surface = FeriaSurface,
    onSurface = FeriaOnSurface,
    surfaceVariant = FeriaSurfaceVariant,
    onSurfaceVariant = FeriaTextSecondary,
    outline = FeriaOutline,
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
