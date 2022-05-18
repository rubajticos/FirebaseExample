package pl.rubajticos.firebaseexample.ui.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.rubajticos.firebaseexample.util.Event
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiEvents = MutableLiveData<Event<DashboardEvent>>()
    val uiEvents: LiveData<Event<DashboardEvent>> = _uiEvents

    fun checkUserLogin() = viewModelScope.launch {
        if (firebaseAuth.currentUser != null) {
            Log.d("MRMR", "Already logged as ${firebaseAuth.currentUser!!.email}")
        } else {
            _uiEvents.value = Event(DashboardEvent.NavigateToLogin)
        }
    }

}