package pl.rubajticos.firebaseexample.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.util.Event
import pl.rubajticos.firebaseexample.util.UiText
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiEvents = MutableLiveData<Event<AccountEvents>>()
    val uiEvents: LiveData<Event<AccountEvents>> = _uiEvents

    fun getAccountData() = viewModelScope.launch {
        val accountData = mutableListOf<AccountInfoItem>()
        firebaseAuth.currentUser?.let { user ->
            user.email?.let {
                accountData.add(
                    AccountInfoItem(UiText.StringResource(R.string.email_label), it)
                )
            }
            user.displayName?.let {
                accountData.add(
                    AccountInfoItem(UiText.StringResource(R.string.name_label), it)
                )
            }
            user.phoneNumber?.let {
                accountData.add(
                    AccountInfoItem(UiText.StringResource(R.string.phone_label), it)
                )
            }
            accountData.add(
                AccountInfoItem(UiText.StringResource(R.string.uid_label), user.uid)
            )
        }

        _uiEvents.value = Event(AccountEvents.ShowAccountInfo(accountData))
    }

}
