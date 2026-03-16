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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.europos_scanner.scanner.CameraPreview
import com.example.europos_scanner.ui.components.GradeClassSelector
import com.example.europos_scanner.ui.components.ResultDialog
import com.example.europos_scanner.ui.components.StudentList
import com.example.europos_scanner.ui.theme.FeriaAccent
import com.example.europos_scanner.ui.theme.FeriaBackground
import com.example.europos_scanner.ui.theme.FeriaPrimary
import com.example.europos_scanner.ui.theme.FeriaSecondary
import com.example.europos_scanner.ui.theme.GlassButton
import com.example.europos_scanner.ui.theme.GlassCard
import com.example.europos_scanner.ui.theme.GlassTextField

@Composable
fun ScannerScreen(viewModel: ScannerViewModel) {
    val state by viewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(FeriaPrimary, FeriaSecondary, FeriaAccent, FeriaBackground)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Моля, сканирайте купон на дете",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onBarcodeScanned = { result ->
                        viewModel.onIntent(ScannerIntent.BarcodeScanned(result.value))
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked = state.isManualInput,
                    onCheckedChange = { viewModel.onIntent(ScannerIntent.ToggleManualInput) },
                    colors = SwitchDefaults.colors(
                        checkedTrackColor = FeriaPrimary,
                        checkedThumbColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ръчно въвеждане",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            if (state.isManualInput) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GlassTextField(
                        value = state.manualInputText,
                        onValueChange = { viewModel.onIntent(ScannerIntent.UpdateManualInput(it)) },
                        label = { Text("Код на ученик") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassButton(
                        onClick = { viewModel.onIntent(ScannerIntent.SubmitManualInput) },
                        text = "Изпрати",
                        enabled = state.manualInputText.isNotBlank() && !state.isProcessingScan
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            GradeClassSelector(
                selectedGrade = state.selectedGrade,
                selectedSection = state.selectedSection,
                onGradeSelected = { viewModel.onIntent(ScannerIntent.SelectGrade(it)) },
                onSectionSelected = { viewModel.onIntent(ScannerIntent.SelectSection(it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth().weight(1f)) {
                if (state.isLoadingStudents) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
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
            onDismiss = { viewModel.onIntent(ScannerIntent.DismissResult) }
        )
    }
}
