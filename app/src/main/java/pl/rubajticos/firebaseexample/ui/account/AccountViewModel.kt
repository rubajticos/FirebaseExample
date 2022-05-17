package pl.rubajticos.firebaseexample.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.rubajticos.firebaseexample.R
import pl.rubajticos.firebaseexample.util.Event
import pl.rubajticos.firebaseexample.util.UiText
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: SignInClient
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
            val providers = StringBuilder()
            user.providerData.forEach {
                providers.append("${it.displayName} | ${it.providerId} | ${it.email}")
                providers.appendLine()
            }
            accountData.add(
                AccountInfoItem(UiText.StringResource(R.string.providers), providers.toString())
            )
        }

        _uiEvents.value = Event(AccountEvents.ShowAccountInfo(accountData))
    }

    fun signOut() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            firebaseAuth.signOut()
            googleSignInClient.signOut()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        _uiEvents.value = Event(AccountEvents.SignOutSuccess)
                    } else {
                        _uiEvents.value =
                            Event(
                                AccountEvents.SignOutError(
                                    UiText.StringResource(R.string.sign_out_error, "")
                                )
                            )
                    }
                }
                .addOnFailureListener {
                    _uiEvents.value =
                        Event(
                            AccountEvents.SignOutError(
                                UiText.StringResource(
                                    R.string.sign_out_error,
                                    it.localizedMessage ?: ""
                                )
                            )
                        )
                }
        }
    }

}
