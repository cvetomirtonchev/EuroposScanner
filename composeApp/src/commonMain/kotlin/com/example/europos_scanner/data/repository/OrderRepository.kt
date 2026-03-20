package com.example.europos_scanner.data.repository

import com.example.europos_scanner.data.model.GetOrdersResponse
import com.example.europos_scanner.data.remote.ApiService

class OrderRepository(private val apiService: ApiService) {

    suspend fun getOrders(
        from: String?,
        to: String?,
        status: String?,
        childrenId: Long?,
        name: String?,
        grade: Int?,
        className: String?,
        page: Int,
        size: Int
    ): Result<GetOrdersResponse> {
        return try {
            val response = apiService.getOrders(
                from = from,
                to = to,
                status = status,
                childrenId = childrenId,
                name = name,
                grade = grade,
                className = className,
                page = page,
                size = size
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
