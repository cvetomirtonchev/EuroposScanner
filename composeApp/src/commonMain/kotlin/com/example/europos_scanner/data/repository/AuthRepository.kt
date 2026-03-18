package com.example.europos_scanner.data.repository

import com.example.europos_scanner.data.model.LoginRequest
import com.example.europos_scanner.data.model.UserDetailsResponse
import com.example.europos_scanner.data.remote.ApiService
import com.example.europos_scanner.domain.session.SessionManager

class AuthRepository(
    private val apiService: ApiService,
    private val sessionManager: SessionManager
) {
    val isLoggedIn: Boolean get() = sessionManager.isLoggedIn

    suspend fun login(username: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(LoginRequest(username, password))
            sessionManager.saveToken(response.token)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserDetails(): Result<UserDetailsResponse> {
        return try {
            Result.success(apiService.getUserDetails())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clearToken()
    }
}
