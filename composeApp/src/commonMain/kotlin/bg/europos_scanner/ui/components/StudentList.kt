package bg.europos_scanner.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bg.europos_scanner.data.model.Student
import bg.europos_scanner.ui.theme.EuroposScannerTheme

@Composable
fun StudentList(
    students: List<Student>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(students, key = { it.id }) { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(colors = CardDefaults.cardColors().copy(containerColor = MaterialTheme.colorScheme.background)) {
                    Column(
                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                    ) {
                        ChildrenRow(student)
                        Spacer(modifier = Modifier.height(4.dp))
                        IdRow(student)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChildrenRow(student: Student) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = student.fullName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(4.dp))
        val info = buildString {
            append("${student.grade}${student.className} клас")
        }
        if (info.isNotEmpty()) {
            Text(
                text = info,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun IdRow(student: Student) {
    Text(
        text = "Код: ${student.id}",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Preview
@Composable
private fun StudentListPreview() {
    EuroposScannerTheme {
        StudentList(
            students = listOf(
                Student(
                    id = 1,
                    firstName = "Иван",
                    lastName = "Петров",
                    grade = 5,
                    className = "А"
                ),
                Student(
                    id = 2,
                    firstName = "Мария",
                    lastName = "Иванова",
                    grade = 5,
                    className = "А"
                ),
                Student(
                    id = 3,
                    firstName = "Георги",
                    lastName = "Стоянов",
                    grade = 5,
                    className = "А"
                ),
                Student(
                    id = 4,
                    firstName = "Елена",
                    lastName = "Димитрова",
                    grade = 5,
                    className = "А"
                ),
            ),
            modifier = Modifier.height(300.dp)
        )
    }
}
