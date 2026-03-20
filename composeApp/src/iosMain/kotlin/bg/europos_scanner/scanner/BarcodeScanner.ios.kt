package bg.europos_scanner.scanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.AVCaptureConnection
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVCaptureDeviceInput
import platform.AVFoundation.AVCaptureMetadataOutput
import platform.AVFoundation.AVCaptureMetadataOutputObjectsDelegateProtocol
import platform.AVFoundation.AVCaptureOutput
import platform.AVFoundation.AVCaptureSession
import platform.AVFoundation.AVCaptureSessionPresetHigh
import platform.AVFoundation.AVCaptureVideoPreviewLayer
import platform.AVFoundation.AVLayerVideoGravityResizeAspectFill
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVMetadataMachineReadableCodeObject
import platform.AVFoundation.AVMetadataObjectTypeCode128Code
import platform.AVFoundation.AVMetadataObjectTypeCode39Code
import platform.AVFoundation.AVMetadataObjectTypeEAN13Code
import platform.AVFoundation.AVMetadataObjectTypeEAN8Code
import platform.AVFoundation.AVMetadataObjectTypeQRCode
import platform.AVFoundation.AVMetadataObjectTypeUPCECode
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_global_queue
import platform.darwin.dispatch_get_main_queue

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
actual fun CameraPreview(
    modifier: Modifier,
    onBarcodeScanned: (BarcodeScanResult) -> Unit
) {
    val captureSession = remember { AVCaptureSession() }
    val delegate = remember { BarcodeMetadataDelegate(onBarcodeScanned) }

    DisposableEffect(Unit) {
        onDispose {
            dispatch_async(dispatch_get_global_queue(0, 0u)) {
                captureSession.stopRunning()
            }
        }
    }

    UIKitView(
        modifier = modifier,
        factory = {
            captureSession.sessionPreset = AVCaptureSessionPresetHigh

            val device = AVCaptureDevice.defaultDeviceWithMediaType(AVMediaTypeVideo)
            if (device != null) {
                val input = AVCaptureDeviceInput.deviceInputWithDevice(device, null)
                if (input != null) {
                    captureSession.addInput(input)
                }
            }

            val metadataOutput = AVCaptureMetadataOutput()
            captureSession.addOutput(metadataOutput)
            metadataOutput.setMetadataObjectsDelegate(delegate, dispatch_get_main_queue())
            metadataOutput.metadataObjectTypes = listOf(
                AVMetadataObjectTypeEAN8Code,
                AVMetadataObjectTypeEAN13Code,
                AVMetadataObjectTypeQRCode,
                AVMetadataObjectTypeCode128Code,
                AVMetadataObjectTypeCode39Code,
                AVMetadataObjectTypeUPCECode
            )

            val previewLayer = AVCaptureVideoPreviewLayer(session = captureSession)
            previewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill

            val containerView = CameraContainerView(previewLayer)
            containerView.layer.addSublayer(previewLayer)

            dispatch_async(dispatch_get_global_queue(0, 0u)) {
                captureSession.startRunning()
            }

            containerView
        }
    )
}

@OptIn(ExperimentalForeignApi::class)
private class CameraContainerView(
    private val previewLayer: AVCaptureVideoPreviewLayer
) : UIView(frame = platform.CoreGraphics.CGRectMake(0.0, 0.0, 0.0, 0.0)) {
    override fun layoutSubviews() {
        super.layoutSubviews()
        previewLayer.frame = this.bounds
    }
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class BarcodeMetadataDelegate(
    private val onBarcodeScanned: (BarcodeScanResult) -> Unit
) : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {

    private var lastScanTime = 0.0
    private val throttleIntervalSec = 3.0

    override fun captureOutput(
        output: AVCaptureOutput,
        didOutputMetadataObjects: List<*>,
        fromConnection: AVCaptureConnection
    ) {
        if (didOutputMetadataObjects.isEmpty()) return

        val metadataObject =
            didOutputMetadataObjects.firstOrNull() as? AVMetadataMachineReadableCodeObject
                ?: return
        val value = metadataObject.stringValue ?: return

        val now = NSDate().timeIntervalSince1970
        if (now - lastScanTime >= throttleIntervalSec) {
            lastScanTime = now
            val format = when (metadataObject.type) {
                AVMetadataObjectTypeEAN8Code -> "EAN_8"
                AVMetadataObjectTypeEAN13Code -> "EAN_13"
                AVMetadataObjectTypeQRCode -> "QR_CODE"
                AVMetadataObjectTypeCode128Code -> "CODE_128"
                AVMetadataObjectTypeCode39Code -> "CODE_39"
                AVMetadataObjectTypeUPCECode -> "UPC_E"
                else -> "UNKNOWN"
            }
            onBarcodeScanned(BarcodeScanResult(value = value, format = format))
        }
    }
}
