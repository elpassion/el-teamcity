package pl.elpassion.eltc

sealed class AppState
object InitialState: AppState()
object LoadingState : AppState()
data class MainState(val builds: List<Build>, val projects: List<Project>) : AppState()
data class SelectProjectsDialogState(val projects: List<Project>) : AppState()

data class LoginState(
        val host: String = "",
        val user: String = "",
        val password: String = "",
        val unknownHost: Boolean = false,
        val invalidCredentials: Boolean = false,
        val networkProblem: Boolean = false
) : AppState()

val LoginState.errorMessage: String?
    get() = when {
        unknownHost -> "Unknown host"
        invalidCredentials -> "Invalid credentials"
        networkProblem -> "Network problem"
        else -> null
    }
