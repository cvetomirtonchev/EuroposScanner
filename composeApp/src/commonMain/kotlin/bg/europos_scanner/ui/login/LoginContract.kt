package bg.europos_scanner.ui.login

data class LoginState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val rememberUsername: Boolean = false
)

sealed class LoginIntent {
    data class UpdateUsername(val username: String) : LoginIntent()
    data class UpdatePassword(val password: String) : LoginIntent()
    data object Login : LoginIntent()
    data object DismissError : LoginIntent()
    data object ToggleRememberUsername : LoginIntent()
}

sealed class LoginEffect {
    data object NavigateToScanner : LoginEffect()
}
