package com.example.europos_scanner.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.europos_scanner.data.remote.ApiException
import com.example.europos_scanner.data.repository.AuthRepository
import com.example.europos_scanner.data.repository.OrderRepository
import com.example.europos_scanner.data.repository.StudentRepository
import com.example.europos_scanner.domain.session.SessionManager
import com.example.europos_scanner.ui.components.ScanResultState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ScannerViewModel(
    private val studentRepository: StudentRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(ScannerState())
    val state: StateFlow<ScannerState> = _state.asStateFlow()

    private val _effect = Channel<ScannerEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadOrders(page = 0)
        loadUserDetails()
    }

    fun onIntent(intent: ScannerIntent) {
        when (intent) {
            is ScannerIntent.BarcodeScanned -> processScannedValue(intent.value)
            is ScannerIntent.UpdateManualInput -> _state.update { it.copy(manualInputText = intent.text) }
            is ScannerIntent.SubmitManualInput -> {
                val text = _state.value.manualInputText.trim()
                if (text.isNotEmpty()) {
                    processScannedValue(text)
                    _state.update { it.copy(manualInputText = "") }
                }
            }

            is ScannerIntent.ToggleCamera -> _state.update { it.copy(isCameraOn = !it.isCameraOn) }
            is ScannerIntent.ToggleManualInput -> _state.update { it.copy(isManualInput = !it.isManualInput) }
            is ScannerIntent.DismissResult -> _state.update { it.copy(scanResult = null) }
            is ScannerIntent.LoadMoreOrders -> {
                val current = _state.value
                if (!current.isLoadingOrders && current.ordersCurrentPage + 1 < current.ordersTotalPages) {
                    loadOrders(page = current.ordersCurrentPage + 1)
                }
            }

            is ScannerIntent.NavigateToAllOrders -> {
                viewModelScope.launch { _effect.send(ScannerEffect.NavigateToAllOrders) }
            }

            is ScannerIntent.NavigateToAllStudents -> {
                viewModelScope.launch { _effect.send(ScannerEffect.NavigateToAllStudents) }
            }

            is ScannerIntent.Logout -> logout()
        }
    }

    private fun loadOrders(page: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingOrders = true) }
            val result = orderRepository.getOrders(
                from = null,
                to = null,
                status = "USED",
                childrenId = null,
                name = null,
                grade = null,
                className = null,
                page = page,
                size = 20
            )
            result.fold(
                onSuccess = { response ->
                    _state.update { current ->
                        val merged = if (page == 0) {
                            response.orders
                        } else {
                            current.orders + response.orders
                        }
                        current.copy(
                            orders = merged,
                            isLoadingOrders = false,
                            ordersCurrentPage = response.pageMeta.currentPage,
                            ordersTotalPages = response.pageMeta.pages,
                            ordersTotalElements = response.pageMeta.totalElements
                        )
                    }
                },
                onFailure = { e ->
                    if (isUnauthorized(e)) {
                        handleUnauthorized()
                    } else {
                        _state.update { it.copy(isLoadingOrders = false) }
                    }
                }
            )
        }
    }

    private fun processScannedValue(rawValue: String) {
        if (_state.value.isProcessingScan) return

        val childrenId = try {
            extractNumber(rawValue).toInt()
        } catch (e: Exception) {
            _state.update {
                it.copy(scanResult = ScanResultState.Error("Невалиден код: $rawValue"))
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isProcessingScan = true) }
            val result = studentRepository.changeOrderStatus(childrenId)
            result.fold(
                onSuccess = { response ->
                    if (response.isUsed) {
                        _state.update {
                            it.copy(
                                scanResult = ScanResultState.Success(
                                    studentName = response.childrenResponse.name
                                ),
                                scannedIds = it.scannedIds + childrenId,
                                isProcessingScan = false
                            )
                        }
                        loadOrders(page = 0)
                    } else {
                        _state.update {
                            it.copy(
                                scanResult = ScanResultState.Error("Неочакван статус: ${response.status}"),
                                isProcessingScan = false
                            )
                        }
                    }
                },
                onFailure = { e ->
                    if (isUnauthorized(e)) {
                        handleUnauthorized()
                    } else {
                        val message = when {
                            e is ApiException && e.code == "ORDER_ITEM_NOT_FOUND" -> "Няма намерена поръчка!"
                            e is ApiException && e.code == "ORDER_ITEM_ALREADY_USED" -> "Поръчката е вече използвана!"
                            e is ApiException -> e.message
                            else -> "Грешка при свързване със сървъра"
                        }
                        _state.update {
                            it.copy(
                                scanResult = ScanResultState.Error(message),
                                isProcessingScan = false
                            )
                        }
                    }
                }
            )
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _effect.send(ScannerEffect.NavigateToLogin)
        }
    }

    private fun loadUserDetails() {
        viewModelScope.launch {
            val result = authRepository.getUserDetails()
            result.fold(
                onSuccess = { details ->
                    _state.update { it.copy(userDetails = details) }
                },
                onFailure = { e ->
                    if (isUnauthorized(e)) {
                        handleUnauthorized()
                    }
                }
            )
        }
    }

    private fun extractNumber(text: String): String {
        val match = Regex("\\d+").find(text) ?: throw IllegalArgumentException("No digits found")
        return match.value
    }

    private fun isUnauthorized(e: Throwable): Boolean =
        e is ApiException && (e.code == "UNAUTHORIZED" || e.code == "401")

    private suspend fun handleUnauthorized() {
        sessionManager.clearToken()
        _effect.send(ScannerEffect.NavigateToLogin)
    }
}
