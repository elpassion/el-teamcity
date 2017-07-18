package pl.elpassion.eltc

interface Repository {
    var authData: AuthData?
    var selectedProjects: List<Project>
}

data class AuthData(val address: String, val credentials: String)