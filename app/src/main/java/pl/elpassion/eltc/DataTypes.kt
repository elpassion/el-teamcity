package pl.elpassion.eltc

sealed class AppState
object NoCredentials : AppState()
object UnknownHost : AppState()
object InvalidCredentials : AppState()
data class Builds(val list: List<Build>) : AppState()

sealed class UserAction
data class SubmitCredentials(val address: String, val user: String, val password: String): UserAction()

sealed class TeamCityApiException : RuntimeException()
object UnknownHostException: TeamCityApiException()
object InvalidCredentialsException: TeamCityApiException()

