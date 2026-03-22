package bg.europos_scanner.data.remote

import bg.europos_scanner.data.model.ErrorResponse
import bg.europos_scanner.data.model.GetOrdersResponse
import bg.europos_scanner.data.model.LoginRequest
import bg.europos_scanner.data.model.OrderStatusRequest
import bg.europos_scanner.data.model.OrderedItemResponse
import bg.europos_scanner.data.model.StudentListResponse
import bg.europos_scanner.data.model.TokenResponse
import bg.europos_scanner.data.model.UserDetailsResponse
import bg.europos_scanner.domain.session.SessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class ApiException(val code: String, override val message: String) : Exception(message)

class ApiService(private val sessionManager: SessionManager) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val client = HttpClient {
        expectSuccess = false
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    suspend fun login(request: LoginRequest): TokenResponse {
        val response = client.post("${ApiConstants.BASE_URL}${ApiConstants.LOGIN}") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return handleResponse(response) { response.body<TokenResponse>() }
    }

    suspend fun getStudents(grade: String?, className: String?): StudentListResponse {
        val token = sessionManager.token ?: throw ApiException("UNAUTHORIZED", "Not logged in")
        val response = client.get("${ApiConstants.BASE_URL}${ApiConstants.STUDENTS}") {
            header("Authorization", "Bearer $token")
            grade?.let {
                parameter("grade", grade)
            }
            className?.let {
                parameter("className", className)
            }
        }
        return handleResponse(response) { response.body<StudentListResponse>() }
    }

    suspend fun getOrders(
        from: String?,
        to: String?,
        status: String?,
        childrenId: Long?,
        name: String?,
        grade: Int?,
        className: String?,
        orderBy: String?,
        page: Int,
        size: Int
    ): GetOrdersResponse {
        val token = sessionManager.token ?: throw ApiException("UNAUTHORIZED", "Not logged in")
        val response = client.get("${ApiConstants.BASE_URL}${ApiConstants.ORDERS}") {
            header("Authorization", token)
            from?.let { parameter("from", it) }
            to?.let { parameter("to", it) }
            status?.let { parameter("status", it) }
            childrenId?.let { parameter("childrenId", it) }
            name?.let { parameter("name", it) }
            grade?.let { parameter("grade", it) }
            className?.let { parameter("className", it) }
            orderBy?.let { parameter("orderBy", it) }
            parameter("page", page)
            parameter("size", size)
        }
        return handleResponse(response) { response.body<GetOrdersResponse>() }
    }

    suspend fun getUserDetails(): UserDetailsResponse {
        val token = sessionManager.token ?: throw ApiException("UNAUTHORIZED", "Not logged in")
        val response = client.get("${ApiConstants.BASE_URL}${ApiConstants.USER_DETAILS}") {
            header("Authorization", token)
        }
        return handleResponse(response) { response.body<UserDetailsResponse>() }
    }

    suspend fun changeOrderStatus(request: OrderStatusRequest): OrderedItemResponse {
        val token = sessionManager.token ?: throw ApiException("UNAUTHORIZED", "Not logged in")
        val response = client.put("${ApiConstants.BASE_URL}${ApiConstants.CHANGE_ORDER_STATUS}") {
            header("Authorization", token)
            contentType(ContentType.Application.Json)
            setBody(request)
        }
        return handleResponse(response) { response.body<OrderedItemResponse>() }
    }

    private suspend fun <T> handleResponse(
        response: HttpResponse,
        successBlock: suspend () -> T
    ): T {
        if (response.status.value in 200..299) {
            return successBlock()
        }
        val bodyText = response.bodyAsText()
        try {
            val errorResponse = json.decodeFromString<ErrorResponse>(bodyText)
            throw ApiException(errorResponse.code, errorResponse.message)
        } catch (e: ApiException) {
            throw e
        } catch (_: Exception) {
            throw ApiException(
                response.status.value.toString(),
                bodyText.ifEmpty { response.status.description }
            )
        }
    }
}
