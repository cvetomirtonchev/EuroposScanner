package bg.europos_scanner.data.repository

import bg.europos_scanner.data.model.GetOrdersResponse
import bg.europos_scanner.data.model.OrderStatusRequest
import bg.europos_scanner.data.model.OrderedItemResponse
import bg.europos_scanner.data.remote.ApiService

class OrderRepository(private val apiService: ApiService) {

    suspend fun getOrders(
        from: String? = null,
        to: String? = null,
        status: String? = null,
        childrenId: Long? = null,
        name: String? = null,
        grade: Int? = null,
        className: String? = null,
        orderBy: String? = null,
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
                orderBy = orderBy,
                page = page,
                size = size
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun changeOrderStatus(childrenId: Int): Result<OrderedItemResponse> {
        return try {
            val response = apiService.changeOrderStatus(OrderStatusRequest(childrenId))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
