package pl.elpassion.eltc

sealed class UserAction

object StartApp : UserAction()
object AcceptLoginError : UserAction()
object RefreshList : UserAction()
object SelectProjects : UserAction()
object ReturnToList : UserAction()
object OpenInWebBrowser : UserAction()
object Logout : UserAction()
object OpenSettings : UserAction()
object RefreshSettings : UserAction()

data class SubmitCredentials(
        val address: String,
        val credentials: String
): UserAction()

data class SubmitProjects(
        val projects: List<Project>
): UserAction()

data class SelectBuild(
        val build: Build
): UserAction()