package pl.elpassion.eltc

sealed class AppState
object NoCredentials : AppState()
object UnknownHost : AppState()
object InvalidCredentials : AppState()
object NetworkProblem : AppState()
data class Builds(val list: List<Build>) : AppState()