package pl.rubajticos.firebaseexample.ui.login_register

import pl.rubajticos.firebaseexample.util.UiText

sealed class LoginRegisterEvent {
    data class Success(val text: UiText) : LoginRegisterEvent()
    data class Error(val text: UiText) : LoginRegisterEvent()
    object Loading : LoginRegisterEvent()
    data class State(val state: LoginRegisterFormState) : LoginRegisterEvent()
    object LoginWithGoogle : LoginRegisterEvent()
    object RegisterWithGoogle : LoginRegisterEvent()
}

sealed class LoginRegisterFormEvent {
    data class EmailChanged(val email: String) : LoginRegisterFormEvent()
    data class PasswordChanged(val password: String) : LoginRegisterFormEvent()
    data class ModeChanged(val mode: Boolean) : LoginRegisterFormEvent()
    object Submit : LoginRegisterFormEvent()
    data class SubmitWithGoogle(val idToken: String) : LoginRegisterFormEvent()
}