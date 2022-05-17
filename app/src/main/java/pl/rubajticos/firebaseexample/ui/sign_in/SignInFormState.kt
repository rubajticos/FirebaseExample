package pl.rubajticos.firebaseexample.ui.sign_in

data class SignInFormState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val registerMode: Boolean = false
)
