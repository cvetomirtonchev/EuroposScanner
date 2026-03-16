package com.example.europos_scanner.domain.session

import com.russhwolf.settings.Settings

class SessionManager(private val settings: Settings) {

    private var _token: String? = settings.getStringOrNull(KEY_TOKEN)
    val token: String? get() = _token
    val isLoggedIn: Boolean get() = _token != null

    val rememberUsername: Boolean
        get() = settings.getBoolean(KEY_REMEMBER_USERNAME, false)

    val savedUsername: String
        get() = settings.getString(KEY_USERNAME, "")

    fun saveToken(token: String) {
        _token = token
        settings.putString(KEY_TOKEN, token)
    }

    fun clearToken() {
        _token = null
        settings.remove(KEY_TOKEN)
    }

    fun saveUsername(username: String) {
        settings.putString(KEY_USERNAME, username)
        settings.putBoolean(KEY_REMEMBER_USERNAME, true)
    }

    fun clearUsername() {
        settings.remove(KEY_USERNAME)
        settings.putBoolean(KEY_REMEMBER_USERNAME, false)
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USERNAME = "saved_username"
        private const val KEY_REMEMBER_USERNAME = "remember_username"
    }
}
