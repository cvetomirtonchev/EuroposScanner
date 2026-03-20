package bg.europos_scanner.ui.scanner

import bg.europos_scanner.data.model.OrderedItemResponse
import bg.europos_scanner.data.model.UserDetailsResponse
import bg.europos_scanner.ui.components.ScanResultState

data class ScannerState(
    val orders: List<OrderedItemResponse> = emptyList(),
    val isLoadingOrders: Boolean = false,
    val ordersCurrentPage: Int = 0,
    val ordersTotalPages: Int = 0,
    val ordersTotalElements: Long = 0,
    val scannedIds: Set<Int> = emptySet(),
    val isCameraOn: Boolean = false,
    val isManualInput: Boolean = false,
    val manualInputText: String = "",
    val scanResult: ScanResultState? = null,
    val isProcessingScan: Boolean = false,
    val userDetails: UserDetailsResponse? = null
)

sealed class ScannerIntent {
    data class BarcodeScanned(val value: String) : ScannerIntent()
    data class UpdateManualInput(val text: String) : ScannerIntent()
    data object SubmitManualInput : ScannerIntent()
    data object ToggleCamera : ScannerIntent()
    data object ToggleManualInput : ScannerIntent()
    data object DismissResult : ScannerIntent()
    data object LoadMoreOrders : ScannerIntent()
    data object NavigateToAllOrders : ScannerIntent()
    data object NavigateToAllStudents : ScannerIntent()
    data object Logout : ScannerIntent()
}

sealed class ScannerEffect {
    data object NavigateToLogin : ScannerEffect()
    data object NavigateToAllOrders : ScannerEffect()
    data object NavigateToAllStudents : ScannerEffect()
}
