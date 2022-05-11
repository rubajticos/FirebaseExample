package pl.rubajticos.firebaseexample.ui.sign_up

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.util.Event
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val _uiEvents = MutableLiveData<Event<SignUpEvent>>()
    val uiEvents: LiveData<Event<SignUpEvent>> = _uiEvents

    fun signUpWithEmailAndPassword(email: String, password: String) = viewModelScope.launch {
        if (email.isBlank()) {
            _uiEvents.value = Event(SignUpEvent.SignUpError(R.string.email_wrong_format))
            return@launch
        }

        if (password.isBlank()) {
            _uiEvents.value = Event(SignUpEvent.SignUpError(R.string.password_error))
            return@launch
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _uiEvents.value = Event(SignUpEvent.SignUpSuccess)
                } else {
                    Log.d("MRMR", task.exception?.localizedMessage ?: "Unknown error")
                    _uiEvents.value = Event(SignUpEvent.SignUpError(R.string.sign_up_error))
                }
            }
    }

}