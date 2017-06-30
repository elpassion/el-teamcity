package pl.elpassion.eltc

sealed class UserAction
object StartApp : UserAction()
data class SubmitCredentials(val address: String, val credentials: String): UserAction()
object RefreshList : UserAction()
object SelectProjects : UserAction()