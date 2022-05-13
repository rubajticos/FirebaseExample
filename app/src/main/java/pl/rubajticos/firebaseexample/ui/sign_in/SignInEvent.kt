package pl.rubajticos.firebaseexample.ui.sign_in

import pl.rubajticos.firebaseexample.util.UiText

sealed class SignInEvent {
    data class SignInSuccess(val text: UiText) : SignInEvent()
    data class SignInError(val text: UiText) : SignInEvent()
    object Loading : SignInEvent()
}
