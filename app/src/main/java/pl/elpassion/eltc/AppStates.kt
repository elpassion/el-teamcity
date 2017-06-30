package pl.elpassion.eltc

sealed class AppState
object InitialState: AppState()
object LoadingState : AppState()
object LoginState : AppState()
object UnknownHostState : AppState()
object InvalidCredentialsState : AppState()
object NetworkProblemState : AppState()
data class MainState(val builds: List<Build>, val projects: List<Project>) : AppState()