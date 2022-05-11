package pl.rubajticos.firebaseexample.ui.sign_up

sealed class SignUpEvent {
    object SignUpSuccess : SignUpEvent()
    data class SignUpError(val resId: Int) : SignUpEvent()
    object Loading : SignUpEvent()
}
