package bg.europos_scanner.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import bg.europos_scanner.data.remote.ApiException
import bg.europos_scanner.data.repository.OrderRepository
import bg.europos_scanner.domain.session.SessionManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AllOrdersViewModel(
    private val orderRepository: OrderRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AllOrdersState())
    val state: StateFlow<AllOrdersState> = _state.asStateFlow()

    private val _effect = Channel<AllOrdersEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadOrders(page = 0)
    }

    fun onIntent(intent: AllOrdersIntent) {
        when (intent) {
            is AllOrdersIntent.UpdateDateFrom -> _state.update { it.copy(filterDateFrom = intent.date) }
            is AllOrdersIntent.UpdateDateTo -> _state.update { it.copy(filterDateTo = intent.date) }
            is AllOrdersIntent.UpdateStatus -> _state.update { it.copy(filterStatus = intent.status) }
            is AllOrdersIntent.UpdateName -> _state.update { it.copy(filterName = intent.name) }
            is AllOrdersIntent.UpdateGrade -> _state.update { it.copy(filterGrade = intent.grade) }
            is AllOrdersIntent.UpdateClassName -> _state.update { it.copy(filterClassName = intent.className) }
            is AllOrdersIntent.ApplyFilters -> loadOrders(page = 0)
            is AllOrdersIntent.LoadMore -> {
                val current = _state.value
                if (!current.isLoading && current.currentPage + 1 < current.totalPages) {
                    loadOrders(page = current.currentPage + 1)
                }
            }
        }
    }

    private fun loadOrders(page: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val s = _state.value
            val gradeInt = s.filterGrade.toIntOrNull()
            val result = orderRepository.getOrders(
                from = null,
                to = null,
                status = s.filterStatus.ifBlank { null },
                childrenId = null,
                name = s.filterName.ifBlank { null },
                grade = gradeInt,
                className = s.filterClassName.ifBlank { null },
                orderBy = null,
                page = page,
                size = 20
            )
            result.fold(
                onSuccess = { response ->
                    _state.update { current ->
                        val merged =
                            if (page == 0) response.orders else current.orders + response.orders
                        current.copy(
                            orders = merged,
                            isLoading = false,
                            currentPage = response.pageMeta.currentPage,
                            totalPages = response.pageMeta.pages,
                            totalElements = response.pageMeta.totalElements
                        )
                    }
                },
                onFailure = { e ->
                    if (e is ApiException && (e.code == "UNAUTHORIZED" || e.code == "401")) {
                        sessionManager.clearToken()
                        _effect.send(AllOrdersEffect.NavigateToLogin)
                    } else {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            )
        }
    }
}
