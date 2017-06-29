package pl.elpassion.eltc

sealed class UserAction
data class SubmitCredentials(val address: String, val credentials: String): UserAction()