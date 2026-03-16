package com.example.europos_scanner.ui.login

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginIntent {
    data class UpdateUsername(val username: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    data object Login : LoginIntent()
    data object DismissError : LoginIntent()
}

sealed class LoginEffect {
    data object NavigateToScanner : LoginEffect()
}
