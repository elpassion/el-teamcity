package pl.elpassion.eltc.login

interface LoginRepository {
    var authData: AuthData?
}

data class AuthData(val address: String, val credentials: String)