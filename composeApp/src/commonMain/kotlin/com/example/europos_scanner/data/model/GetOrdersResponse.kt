package com.example.europos_scanner.data.model

import kotlinx.serialization.Serializable

@Serializable
data class GetOrdersResponse(
    val orders: List<OrderedItemResponse>,
    val pageMeta: PageMeta
)

@Serializable
data class OrderedItemResponse(
    val orderedItemId: Long,
    val orderedOn: String? = null,
    val forDate: String? = null,
    val menuName: String? = null,
    val menuId: Long? = null,
    val amount: Long = 0,
    val status: String,
    val childrenResponse: OrderChildrenResponse? = null,
    val isRefundable: Boolean = false
) {
    val childName: String
        get() {
            val first = childrenResponse?.firstName ?: ""
            val last = childrenResponse?.lastName ?: ""
            return "$first $last".trim()
        }
}

@Serializable
data class OrderChildrenResponse(
    val id: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val grade: Int? = null,
    val className: String? = null
)

@Serializable
data class PageMeta(
    val currentPage: Int,
    val pages: Int,
    val totalElements: Long
)
