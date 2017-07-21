package pl.elpassion.eltc.api

sealed class TeamCityApiException : RuntimeException()
object UnknownHostException: TeamCityApiException()
object InvalidCredentialsException: TeamCityApiException()
object NetworkTimeoutException: TeamCityApiException()