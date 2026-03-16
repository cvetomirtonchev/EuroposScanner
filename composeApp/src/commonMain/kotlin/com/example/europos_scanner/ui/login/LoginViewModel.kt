package com.example.europos_scanner.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.europos_scanner.data.remote.ApiException
import com.example.europos_scanner.data.repository.AuthRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UpdateUsername -> _state.update { it.copy(username = intent.username, error = null) }
            is LoginIntent.UpdatePassword -> _state.update { it.copy(password = intent.password, error = null) }
            is LoginIntent.Login -> login()
            is LoginIntent.DismissError -> _state.update { it.copy(error = null) }
        }
    }

    private fun login() {
        val currentState = _state.value
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _state.update { it.copy(error = "Моля, попълнете всички полета") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = authRepository.login(currentState.username, currentState.password)
            result.fold(
                onSuccess = {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(LoginEffect.NavigateToScanner)
                },
                onFailure = { e ->
                    val message = when (e) {
                        is ApiException -> e.message
                        else -> "Грешка при свързване със сървъра"
                    }
                    _state.update { it.copy(isLoading = false, error = message) }
                }
            )
        }
    }
}
