package pl.rubajticos.firebaseexample.ui.sign_in_up

import pl.rubajticos.firebaseexample.util.UiText

data class SignInFormState(
    val email: String = "",
    val emailError: UiText? = null,
    val password: String = "",
    val passwordError: UiText? = null,
    val registerMode: Boolean = false
)
