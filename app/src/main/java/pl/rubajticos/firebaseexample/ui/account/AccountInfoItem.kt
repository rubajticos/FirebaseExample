package pl.rubajticos.firebaseexample.ui.account

import pl.rubajticos.firebaseexample.util.UiText

data class AccountInfoItem(
    val label: UiText,
    val value: String
)

data class AccountInfoUiItem(
    val label: String,
    val value: String
)