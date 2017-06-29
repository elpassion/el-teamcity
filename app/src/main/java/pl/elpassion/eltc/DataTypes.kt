package pl.elpassion.eltc

sealed class AppState
object NoCredentials : AppState()
object UnknownHost : AppState()

sealed class UserAction
data class SubmitCredentials(val address: String, val user: String, val password: String): UserAction()



