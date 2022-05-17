package pl.rubajticos.firebaseexample.ui.account

sealed class AccountEvents {

    data class ShowAccountInfo(val infos: List<AccountInfoItem>) : AccountEvents()

}