package pl.rubajticos.firebaseexample.ui.login_register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.model.ValidateResult
import pl.rubajticos.firebaseexample.util.Event
import pl.rubajticos.firebaseexample.util.UiText
import javax.inject.Inject

@HiltViewModel
class LoginRegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiEvents = MutableLiveData<Event<LoginRegisterEvent>>()
    val uiEvents: LiveData<Event<LoginRegisterEvent>> = _uiEvents
    var state = LoginRegisterFormState()

    fun onEvent(event: LoginRegisterFormEvent) = viewModelScope.launch {
        when (event) {
            is LoginRegisterFormEvent.EmailChanged -> {
                state = state.copy(email = event.email)
                emitState()
            }
            is LoginRegisterFormEvent.ModeChanged -> {
                state = state.copy(registerMode = event.mode)
                emitState()
            }
            is LoginRegisterFormEvent.PasswordChanged -> {
                state = state.copy(password = event.password)
                emitState()
            }
            LoginRegisterFormEvent.Submit -> {
                if (state.registerMode) {
                    registerWithEmailAndPassword()
                } else {
                    loginWithEmailAndPassword()
                }
            }
            is LoginRegisterFormEvent.SubmitWithGoogle -> {
                if (state.registerMode) {
                    registerWithGoogle(event.idToken)
                } else {
                    loginWithGoogle(event.idToken)
                }
            }
        }
    }

    private fun emitState() {
        _uiEvents.value = Event(LoginRegisterEvent.State(state))
    }

    private fun loginWithEmailAndPassword() = viewModelScope.launch {
        _uiEvents.value = Event(LoginRegisterEvent.Loading)
        val emailValidation = validateEmail(state.email)
        val passwordValidation = validatePassword(state.password)

        val hasError = listOf(emailValidation, passwordValidation).any { !it.isSuccessful }
        if (hasError) {
            state = state.copy(
                emailError = emailValidation.error,
                passwordError = passwordValidation.error
            )
            emitState()
            return@launch
        }

        firebaseAuth.signInWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiEvents.value = Event(
                        LoginRegisterEvent.Success(
                            UiText.StringResource(
                                R.string.login_success,
                                getCurrentUserEmail()
                            )
                        )
                    )
                } else {
                    Log.d("MRMR", task.exception?.localizedMessage ?: "Unknown error")
                    _uiEvents.value =
                        Event(
                            LoginRegisterEvent.Error(
                                UiText.DynamicString(
                                    task.exception?.localizedMessage ?: "Unknown error"
                                )
                            )
                        )
                }
            }
            .addOnFailureListener {
                _uiEvents.value = Event(
                    LoginRegisterEvent.Error(
                        UiText.DynamicString(
                            it.localizedMessage ?: "Login error"
                        )
                    )
                )
                Log.d("MRMR", "loginWithEmailAndPassword failure -> ${it.localizedMessage}")
            }
    }

    private fun registerWithEmailAndPassword() = viewModelScope.launch {
        _uiEvents.value = Event(LoginRegisterEvent.Loading)
        val emailValidation = validateEmail(state.email)
        val passwordValidation = validatePassword(state.password)

        val hasError = listOf(emailValidation, passwordValidation).any { !it.isSuccessful }
        if (hasError) {
            state = state.copy(
                emailError = emailValidation.error,
                passwordError = passwordValidation.error
            )
            emitState()
            return@launch
        }

        firebaseAuth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiEvents.value = Event(
                        LoginRegisterEvent.Success(
                            UiText.StringResource(
                                R.string.register_success,
                                getCurrentUserEmail()
                            )
                        )
                    )
                } else {
                    _uiEvents.value =
                        Event(
                            LoginRegisterEvent.Error(
                                UiText.DynamicString(
                                    task.exception?.localizedMessage ?: "Unknown error"
                                )
                            )
                        )
                }
            }
            .addOnFailureListener {
                _uiEvents.value = Event(
                    LoginRegisterEvent.Error(
                        UiText.DynamicString(
                            it.localizedMessage ?: "Register error"
                        )
                    )
                )
                Log.d("MRMR", "registerWithEmailAndPassword failure -> ${it.localizedMessage}")
            }
    }

    private fun validateEmail(email: String): ValidateResult {
        if (email.isBlank()) {
            return ValidateResult(false, UiText.StringResource(R.string.email_wrong_format))
        }
        return ValidateResult(isSuccessful = true)
    }

    private fun validatePassword(password: String): ValidateResult {
        if (password.isBlank()) {
            return ValidateResult(false, UiText.StringResource(R.string.password_error))
        }
        return ValidateResult(isSuccessful = true)
    }


    private fun loginWithGoogle(idToken: String) = viewModelScope.launch {
        _uiEvents.value = Event(LoginRegisterEvent.Loading)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _uiEvents.value = Event(
                        LoginRegisterEvent.Success(
                            UiText.StringResource(
                                R.string.login_success,
                                getCurrentUserEmail()
                            )
                        )
                    )
                } else {
                    LoginRegisterEvent.Error(
                        UiText.StringResource(R.string.login_error)
                    )
                }
            }
            .addOnFailureListener {
                _uiEvents.value = Event(
                    LoginRegisterEvent.Error(
                        UiText.DynamicString(
                            it.localizedMessage ?: "Login error"
                        )
                    )
                )
                Log.d("MRMR", "loginWithCredential failure -> ${it.localizedMessage}")
            }
    }

    private fun registerWithGoogle(idToken: String) = viewModelScope.launch {
        _uiEvents.value = Event(LoginRegisterEvent.Loading)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _uiEvents.value = Event(
                        LoginRegisterEvent.Success(
                            UiText.StringResource(
                                R.string.register_success,
                                getCurrentUserEmail()
                            )
                        )
                    )
                } else {
                    LoginRegisterEvent.Error(
                        UiText.StringResource(R.string.register_error)
                    )
                }
            }
            .addOnFailureListener {
                _uiEvents.value = Event(
                    LoginRegisterEvent.Error(
                        UiText.DynamicString(
                            it.localizedMessage ?: "Register error"
                        )
                    )
                )
                Log.d("MRMR", "Register with Google failure -> ${it.localizedMessage}")
            }
    }

    private fun getCurrentUserEmail() = firebaseAuth.currentUser?.email ?: "UNKNOWN"

    fun onGoogleButtonClick() = viewModelScope.launch {
        if (state.registerMode) {
            _uiEvents.value = Event(LoginRegisterEvent.RegisterWithGoogle)
        } else {
            _uiEvents.value = Event(LoginRegisterEvent.LoginWithGoogle)
        }
    }

}