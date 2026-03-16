package com.example.europos_scanner.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.europos_scanner.ui.theme.GlassDropdown

val GRADES = (1..12).map { "$it Клас" }
val CLASS_SECTIONS = listOf("А", "Б", "В", "Г", "Д", "Е", "Ж", "З")

@Composable
fun GradeClassSelector(
    selectedGrade: String,
    selectedSection: String,
    onGradeSelected: (String) -> Unit,
    onSectionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GlassDropdown(
            items = GRADES,
            selectedItem = selectedGrade,
            onItemSelected = onGradeSelected,
            label = "Клас",
            modifier = Modifier.weight(1f)
        )
        GlassDropdown(
            items = CLASS_SECTIONS,
            selectedItem = selectedSection,
            onItemSelected = onSectionSelected,
            label = "Паралелка",
            modifier = Modifier.weight(1f)
        )
    }
}
