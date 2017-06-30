package pl.elpassion.eltc

sealed class AppState
object InitialState: AppState()
object LoadingState : AppState()
object LoginState : AppState()
object UnknownHostState : AppState()
object InvalidCredentialsState : AppState()
object NetworkProblemState : AppState()
data class BuildsState(val list: List<Build>) : AppState()