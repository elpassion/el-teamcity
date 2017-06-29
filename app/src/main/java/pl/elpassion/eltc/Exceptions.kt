package pl.elpassion.eltc

sealed class TeamCityApiException : RuntimeException()
object UnknownHostException: TeamCityApiException()
object InvalidCredentialsException: TeamCityApiException()
object NetworkTimeoutException: TeamCityApiException()