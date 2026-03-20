package bg.europos_scanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bg.europos_scanner.ui.theme.EuroposScannerTheme

sealed class ScanResultState {
    data class Success(val studentName: String, val message: String = "Ученикът е успешно маркиран!") : ScanResultState()
    data class Error(val message: String) : ScanResultState()
}

@Composable
fun ResultDialog(
    result: ScanResultState?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (result == null) return

    val isSuccess = result is ScanResultState.Success
    val accentColor = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSuccess) "\u2714" else "\u2716",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
        },
        title = {
            when (result) {
                is ScanResultState.Success -> Text(
                    text = result.studentName,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                is ScanResultState.Error -> Text(
                    text = "Грешка",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            when (result) {
                is ScanResultState.Success -> Text(
                    text = result.message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                is ScanResultState.Error -> Text(
                    text = result.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun ResultDialogSuccessPreview() {
    EuroposScannerTheme {
        ResultDialog(
            result = ScanResultState.Success(studentName = "Иван Петров"),
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun ResultDialogErrorPreview() {
    EuroposScannerTheme {
        ResultDialog(
            result = ScanResultState.Error(message = "Поръчката е вече използвана!"),
            onDismiss = {}
        )
    }
}
