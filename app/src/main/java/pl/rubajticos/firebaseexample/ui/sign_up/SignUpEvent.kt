package pl.rubajticos.firebaseexample.ui.sign_up

import pl.rubajticos.firebaseexample.util.UiText

sealed class SignUpEvent {
    data class SignUpSuccess(val text: UiText) : SignUpEvent()
    data class SignUpError(val text: UiText) : SignUpEvent()
    object Loading : SignUpEvent()
}
