package com.example.europos_scanner.ui.students

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.europos_scanner.data.model.Student
import com.example.europos_scanner.data.remote.ApiException
import com.example.europos_scanner.data.repository.StudentRepository
import com.example.europos_scanner.domain.session.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentsState(
    val selectedGrade: String = "1 Клас",
    val selectedSection: String = "Всички",
    val students: List<Student> = emptyList(),
    val isLoading: Boolean = false
)

sealed class StudentsIntent {
    data class SelectGrade(val grade: String) : StudentsIntent()
    data class SelectSection(val section: String) : StudentsIntent()
}

sealed class StudentsEffect {
    data object NavigateToLogin : StudentsEffect()
}

class StudentsViewModel(
    private val studentRepository: StudentRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(StudentsState())
    val state: StateFlow<StudentsState> = _state.asStateFlow()

    private val _effect = Channel<StudentsEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadStudents()
    }

    fun onIntent(intent: StudentsIntent) {
        when (intent) {
            is StudentsIntent.SelectGrade -> {
                _state.update { it.copy(selectedGrade = intent.grade) }
                loadStudents()
            }
            is StudentsIntent.SelectSection -> {
                _state.update { it.copy(selectedSection = intent.section) }
                loadStudents()
            }
        }
    }

    private fun loadStudents() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val gradeNumber = extractNumber(_state.value.selectedGrade)
            val section = _state.value.selectedSection
            val className = if (section == "Всички" || section == "ВСИЧКИ") null else section
            val result = studentRepository.getStudents(
                grade = gradeNumber,
                className = className
            )
            result.fold(
                onSuccess = { students ->
                    _state.update { it.copy(students = students, isLoading = false) }
                },
                onFailure = { e ->
                    if (e is ApiException && (e.code == "UNAUTHORIZED" || e.code == "401")) {
                        sessionManager.clearToken()
                        _effect.send(StudentsEffect.NavigateToLogin)
                    } else {
                        _state.update { it.copy(students = emptyList(), isLoading = false) }
                    }
                }
            )
        }
    }

    private fun extractNumber(text: String): String {
        val match = Regex("\\d+").find(text) ?: throw IllegalArgumentException("No digits found")
        return match.value
    }
}
