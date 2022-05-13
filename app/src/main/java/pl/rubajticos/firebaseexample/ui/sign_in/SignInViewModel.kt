package pl.rubajticos.firebaseexample.ui.sign_up

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
import pl.rubajticos.firebaseexample.ui.sign_in.SignInEvent
import pl.rubajticos.firebaseexample.util.Event
import pl.rubajticos.firebaseexample.util.UiText
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiEvents = MutableLiveData<Event<SignInEvent>>()
    val uiEvents: LiveData<Event<SignInEvent>> = _uiEvents

    fun signInWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        _uiEvents.value = Event(SignInEvent.Loading)
        if (email.isBlank()) {
            _uiEvents.value =
                Event(SignInEvent.SignInError(UiText.StringResource(R.string.email_wrong_format)))
            return@launch
        }

        if (password.isBlank()) {
            _uiEvents.value =
                Event(SignInEvent.SignInError(UiText.StringResource(R.string.password_error)))
            return@launch
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiEvents.value = Event(
                        SignInEvent.SignInSuccess(
                            UiText.StringResource(
                                R.string.sign_in_success,
                                getCurrentUserEmail()
                            )
                        )
                    )
                } else {
                    Log.d("MRMR", task.exception?.localizedMessage ?: "Unknown error")
                    _uiEvents.value =
                        Event(
                            SignInEvent.SignInError(
                                UiText.DynamicString(
                                    task.exception?.localizedMessage ?: "Unknown error"
                                )
                            )
                        )
                }
            }
            .addOnFailureListener {
                _uiEvents.value = Event(
                    SignInEvent.SignInError(
                        UiText.DynamicString(
                            it.localizedMessage ?: "SignIn error"
                        )
                    )
                )
                Log.d("MRMR", "signInWithEmailAndPassword failure -> ${it.localizedMessage}")
            }
    }

    fun signUpWithGoogle(idToken: String) = viewModelScope.launch {
        _uiEvents.value = Event(SignInEvent.Loading)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    _uiEvents.value = Event(
                        SignInEvent.SignInSuccess(
                            UiText.StringResource(
                                R.string.sign_in_success,
                                getCurrentUserEmail()
                            )
                        )
                    )
                } else {
                    SignUpEvent.SignUpError(
                        UiText.StringResource(R.string.sign_in_error)
                    )
                }
            }
            .addOnFailureListener {
                _uiEvents.value = Event(
                    SignInEvent.SignInError(
                        UiText.DynamicString(
                            it.localizedMessage ?: "SignIn error"
                        )
                    )
                )
                Log.d("MRMR", "signInWithCredential failure -> ${it.localizedMessage}")
            }
    }

    private fun getCurrentUserEmail() = firebaseAuth.currentUser?.email ?: "UNKNOWN"

}