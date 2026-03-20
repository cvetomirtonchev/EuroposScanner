package bg.europos_scanner.scanner

import androidx.compose.runtime.Composable

data class CameraPermissionState(
    val hasPermission: Boolean,
    val requestPermission: () -> Unit
)

@Composable
expect fun rememberCameraPermissionState(): CameraPermissionState
