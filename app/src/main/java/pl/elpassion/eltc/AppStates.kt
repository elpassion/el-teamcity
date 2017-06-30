package pl.elpassion.eltc

sealed class AppState
object NoCredentialsState : AppState()
object UnknownHostState : AppState()
object InvalidCredentialsState : AppState()
object NetworkProblemState : AppState()
data class BuildsState(val list: List<Build>) : AppState()