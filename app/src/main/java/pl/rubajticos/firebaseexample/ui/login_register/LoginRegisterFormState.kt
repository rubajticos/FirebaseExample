package pl.rubajticos.firebaseexample.ui.login_register

import pl.rubajticos.firebaseexample.util.UiText

data class LoginRegisterFormState(
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val registerMode: Boolean = false
)
