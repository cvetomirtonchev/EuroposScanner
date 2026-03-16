package com.example.europos_scanner.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.europos_scanner.data.model.Student
import com.example.europos_scanner.ui.theme.FeriaPrimary

@Composable
fun StudentList(
    students: List<Student>,
    scannedIds: Set<Int>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(students, key = { it.id }) { student ->
            val isScanned = student.id in scannedIds
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isScanned,
                    onCheckedChange = null,
                    colors = CheckboxDefaults.colors(
                        checkedColor = FeriaPrimary,
                        uncheckedColor = Color.White.copy(alpha = 0.5f),
                        checkmarkColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = student.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isScanned) Color.White else Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}
