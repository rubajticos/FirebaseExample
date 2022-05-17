package pl.rubajticos.firebaseexample.ui.account

import pl.rubajticos.firebaseexample.util.UiText

sealed class AccountEvents {

    data class ShowAccountInfo(val info: List<AccountInfoItem>) : AccountEvents()
    object SignOutSuccess : AccountEvents()
    data class SignOutError(val text: UiText) : AccountEvents()
}