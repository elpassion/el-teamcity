package pl.elpassion.eltc

interface Repository {
    var authData: AuthData?
}

data class AuthData(val address: String, val credentials: String)