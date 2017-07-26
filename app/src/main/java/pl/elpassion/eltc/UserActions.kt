package pl.elpassion.eltc

sealed class UserAction

object StartApp : UserAction()
object AcceptLoginError : UserAction()
object RefreshList : UserAction()
object SelectProjects : UserAction()
object ReturnToList : UserAction()
object Logout : UserAction()

data class SubmitCredentials(
        val address: String,
        val credentials: String
): UserAction()

data class AutoRefresh(
        val isEnabled: Boolean
): UserAction()

data class SubmitProjects(
        val projects: List<Project>
): UserAction()

data class SelectBuild(
        val build: Build
): UserAction()