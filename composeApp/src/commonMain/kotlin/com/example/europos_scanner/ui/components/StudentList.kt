package com.example.europos_scanner.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.europos_scanner.data.model.Student
import com.example.europos_scanner.ui.theme.EuroposScannerTheme

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
                    onCheckedChange = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = student.fullName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isScanned)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview
@Composable
private fun StudentListPreview() {
    EuroposScannerTheme {
        StudentList(
            students = listOf(
                Student(id = 1, firstName = "Иван", lastName = "Петров", grade = "5", className = "А"),
                Student(id = 2, firstName = "Мария", lastName = "Иванова", grade = "5", className = "А"),
                Student(id = 3, firstName = "Георги", lastName = "Стоянов", grade = "5", className = "А"),
                Student(id = 4, firstName = "Елена", lastName = "Димитрова", grade = "5", className = "А"),
            ),
            scannedIds = setOf(1, 3),
            modifier = Modifier.height(300.dp)
        )
    }
}
