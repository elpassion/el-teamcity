package pl.elpassion.eltc

sealed class AppState
object InitialState : AppState()
object LoadingState : AppState()
data class BuildsState(val builds: List<Build>, val projects: List<Project>) : AppState()
data class SelectProjectsDialogState(val projects: List<Project>) : AppState()

data class LoginState(
        val host: String = "",
        val user: String = "",
        val password: String = "",
        val error: Error? = null
) : AppState() {
    enum class Error(val message: String) {
        UNKNOWN_HOST("Unknown host"),
        INVALID_CREDENTIALS("Invalid credentials"),
        NETWORK_PROBLEM("Network problem")
    }
}
