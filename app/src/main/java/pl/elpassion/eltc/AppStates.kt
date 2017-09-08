package pl.elpassion.eltc

import pl.elpassion.eltc.builds.SelectableProject
import pl.elpassion.eltc.settings.Settings

sealed class AppState

object InitialState : AppState()
object LoadingBuildsState : AppState()

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

data class BuildsState(
        val builds: List<Build>,
        val projects: List<Project>,
        val recapSettings: RecapSettings
) : AppState() {
    data class RecapSettings(
            val isEnabled: Boolean,
            val durationInMinutes: Int,
            val filteredProjects: List<Project>?
    ) {
        companion object {
            val DEFAULT = RecapSettings(
                    isEnabled = Settings.DEFAULT.areNotificationsEnabled,
                    durationInMinutes = Settings.DEFAULT.notificationsFrequencyInMinutes,
                    filteredProjects = null)
        }
    }
}

data class SelectProjectsDialogState(
        val projects: List<SelectableProject>
) : AppState()

data class LoadingDetailsState(
        val build: Build
) : AppState()

data class DetailsState(
        val build: Build,
        val changes: List<Change>,
        val tests: List<TestDetails>,
        val problems: List<ProblemOccurrence>,
        val isWebBrowserVisible: Boolean = false
) : AppState()

data class SettingsState(
        val settings: Settings
) : AppState()