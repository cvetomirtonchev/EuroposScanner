package bg.europos_scanner.scanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class BarcodeScanResult(
    val value: String,
    val format: String = "UNKNOWN"
)

@Composable
expect fun CameraPreview(
    modifier: Modifier = Modifier,
    onBarcodeScanned: (BarcodeScanResult) -> Unit
)
