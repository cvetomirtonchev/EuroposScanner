package bg.europos_scanner.ui.orders

import bg.europos_scanner.data.model.OrderedItemResponse

data class AllOrdersState(
    val orders: List<OrderedItemResponse> = emptyList(),
    val isLoading: Boolean = false,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val totalElements: Long = 0,
    val filterDateFrom: String = "",
    val filterDateTo: String = "",
    val filterStatus: String = "",
    val filterName: String = "",
    val filterGrade: String = "",
    val filterClassName: String = ""
)

sealed class AllOrdersIntent {
    data class UpdateDateFrom(val date: String) : AllOrdersIntent()
    data class UpdateDateTo(val date: String) : AllOrdersIntent()
    data class UpdateStatus(val status: String) : AllOrdersIntent()
    data class UpdateName(val name: String) : AllOrdersIntent()
    data class UpdateGrade(val grade: String) : AllOrdersIntent()
    data class UpdateClassName(val className: String) : AllOrdersIntent()
    data object ApplyFilters : AllOrdersIntent()
    data object LoadMore : AllOrdersIntent()
}

sealed class AllOrdersEffect {
    data object NavigateToLogin : AllOrdersEffect()
}