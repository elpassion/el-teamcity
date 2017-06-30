package pl.elpassion.eltc

sealed class UserAction
object StartApp : UserAction() // TODO: FinishApp action where we cancel auto refresh etc?
data class SubmitCredentials(val address: String, val credentials: String): UserAction()
data class AutoRefresh(val enable: Boolean): UserAction()
object RefreshList : UserAction()
object SelectProjects : UserAction()