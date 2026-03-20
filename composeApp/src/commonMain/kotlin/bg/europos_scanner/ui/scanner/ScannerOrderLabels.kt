package bg.europos_scanner.ui.scanner

import kotlinx.datetime.LocalDateTime

fun formatScannerTakenAt(iso: String?): String? {
    val trimmed = iso?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val ldt = try {
        LocalDateTime.parse(trimmed)
    } catch (_: Exception) {
        return null
    }
    val timeStr =
        "${ldt.hour.toString().padStart(2, '0')}:${ldt.minute.toString().padStart(2, '0')}"
    val date = ldt.date
    val dateStr =
        "${date.day.toString().padStart(2, '0')}.${(date.month.ordinal + 1).toString().padStart(2, '0')}.${date.year}"
    return "Взет в $timeStr\nна $dateStr"
}
