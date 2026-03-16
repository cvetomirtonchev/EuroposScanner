package com.example.europos_scanner.domain.session

class SessionManager {
    private var _token: String? = null
    val token: String? get() = _token
    val isLoggedIn: Boolean get() = _token != null

    fun saveToken(token: String) {
        _token = token
    }

    fun clearToken() {
        _token = null
    }
}
