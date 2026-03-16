package com.example.europos_scanner.ui.scanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.europos_scanner.data.model.Student
import com.example.europos_scanner.scanner.CameraPreview
import com.example.europos_scanner.ui.components.GradeClassSelector
import com.example.europos_scanner.ui.components.ResultDialog
import com.example.europos_scanner.ui.components.StudentList
import com.example.europos_scanner.ui.theme.EuroposScannerTheme

@Composable
fun ScannerScreen(viewModel: ScannerViewModel) {
    val state by viewModel.state.collectAsState()

    ScannerContent(
        state = state,
        onIntent = viewModel::onIntent,
        cameraSlot = {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onBarcodeScanned = { result ->
                    viewModel.onIntent(ScannerIntent.BarcodeScanned(result.value))
                }
            )
        }
    )
}

@Composable
fun ScannerContent(
    state: ScannerState,
    onIntent: (ScannerIntent) -> Unit,
    cameraSlot: @Composable () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Моля, сканирайте купон на дете",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    cameraSlot()
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Switch(
                        checked = state.isManualInput,
                        onCheckedChange = { onIntent(ScannerIntent.ToggleManualInput) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ръчно въвеждане",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                if (state.isManualInput) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = state.manualInputText,
                            onValueChange = { onIntent(ScannerIntent.UpdateManualInput(it)) },
                            label = { Text("Код на ученик") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onIntent(ScannerIntent.SubmitManualInput) },
                            enabled = state.manualInputText.isNotBlank() && !state.isProcessingScan
                        ) {
                            Text("Изпрати")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                GradeClassSelector(
                    selectedGrade = state.selectedGrade,
                    selectedSection = state.selectedSection,
                    onGradeSelected = { onIntent(ScannerIntent.SelectGrade(it)) },
                    onSectionSelected = { onIntent(ScannerIntent.SelectSection(it)) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    if (state.isLoadingStudents) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        StudentList(
                            students = state.students,
                            scannedIds = state.scannedIds,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            ResultDialog(
                result = state.scanResult,
                onDismiss = { onIntent(ScannerIntent.DismissResult) }
            )
        }
    }
}

@Composable
private fun CameraPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Camera Preview",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val sampleStudents = listOf(
    Student(id = 1, firstName = "Иван", lastName = "Петров", grade = "5", className = "А"),
    Student(id = 2, firstName = "Мария", lastName = "Иванова", grade = "5", className = "А"),
    Student(id = 3, firstName = "Георги", lastName = "Стоянов", grade = "5", className = "А"),
    Student(id = 4, firstName = "Елена", lastName = "Димитрова", grade = "5", className = "А"),
)

@Preview
@Composable
private fun ScannerContentPreview() {
    EuroposScannerTheme {
        ScannerContent(
            state = ScannerState(
                students = sampleStudents,
                scannedIds = setOf(1, 3)
            ),
            onIntent = {},
            cameraSlot = { CameraPlaceholder() }
        )
    }
}

@Preview
@Composable
private fun ScannerContentManualInputPreview() {
    EuroposScannerTheme {
        ScannerContent(
            state = ScannerState(
                students = sampleStudents,
                scannedIds = setOf(1),
                isManualInput = true,
                manualInputText = "12345"
            ),
            onIntent = {},
            cameraSlot = { CameraPlaceholder() }
        )
    }
}
