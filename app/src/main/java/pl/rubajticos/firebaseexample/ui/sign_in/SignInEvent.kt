package pl.rubajticos.firebaseexample.ui.sign_in

import pl.rubajticos.firebaseexample.util.UiText

sealed class SignInEvent {
    data class SignInSuccess(val text: UiText) : SignInEvent()
    data class SignInError(val text: UiText) : SignInEvent()
    object Loading : SignInEvent()
    data class State(val state: SignInFormState): SignInEvent()
}

sealed class SignInFormEvent {
    data class EmailChanged(val email: String) : SignInFormEvent()
    data class PasswordChanged(val password: String) : SignInFormEvent()
    data class ModeChanged(val mode: Boolean) : SignInFormEvent()
    object Submit : SignInFormEvent()
    data class SubmitWithGoogle(val idToken: String) : SignInFormEvent()
}