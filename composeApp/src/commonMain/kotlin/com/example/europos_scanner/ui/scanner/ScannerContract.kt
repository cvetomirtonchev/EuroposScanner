package com.example.europos_scanner.ui.scanner

import com.example.europos_scanner.data.model.Student
import com.example.europos_scanner.ui.components.ScanResultState

data class ScannerState(
    val selectedGrade: String = "1 Клас",
    val selectedSection: String = "А",
    val students: List<Student> = emptyList(),
    val scannedIds: Set<Int> = emptySet(),
    val isLoadingStudents: Boolean = false,
    val isManualInput: Boolean = false,
    val manualInputText: String = "",
    val scanResult: ScanResultState? = null,
    val isProcessingScan: Boolean = false
)

sealed class ScannerIntent {
    data class SelectGrade(val grade: String) : ScannerIntent()
    data class SelectSection(val section: String) : ScannerIntent()
    data class BarcodeScanned(val value: String) : ScannerIntent()
    data class UpdateManualInput(val text: String) : ScannerIntent()
    data object SubmitManualInput : ScannerIntent()
    data object ToggleManualInput : ScannerIntent()
    data object DismissResult : ScannerIntent()
    data object LoadStudents : ScannerIntent()
}

sealed class ScannerEffect {
    data object NavigateToLogin : ScannerEffect()
}
