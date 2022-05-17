package pl.rubajticos.firebaseexample.model

import pl.rubajticos.firebaseexample.util.UiText

data class ValidateResult(val isSuccessful: Boolean, val error: UiText? = null)
