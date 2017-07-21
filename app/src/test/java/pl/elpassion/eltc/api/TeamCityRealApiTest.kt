@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.api

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import pl.elpassion.eltc.login.AuthData
import pl.elpassion.eltc.login.LoginRepository

@Ignore
class TeamCityRealApiTest {

    private val ADDRESS = "http://192.168.1.155:8111"
    private val CREDENTIALS = "dXNlcjpwYXNz"
    private val INVALID_CREDENTIALS = "invalid credentials"
    private val loginRepository = mock<LoginRepository>()
    private val teamCityApi = TeamCityApiImpl(loginRepository)

    @Before
    fun setup() {
        whenever(loginRepository.authData).thenReturn(AuthData(ADDRESS, CREDENTIALS))
    }

    @Test
    fun `Get builds after call to real TeamCity API`() {
        teamCityApi
                .getBuilds()
                .getAndPrint()
    }

    @Test
    fun `Get builds of the specified project after call to real TeamCity API`() {
        teamCityApi
                .getBuildsForProjects(projectIds = listOf("TeamcityAndroidClient"))
                .getAndPrint()
    }

    @Test
    fun `Get single build after call to real TeamCity API`() {
        teamCityApi
                .getBuild(id = 1)
                .getAndPrint()
    }

    @Test
    fun `Get all tests per build after call to real TeamCity API`() {
        teamCityApi
                .getTests(buildId = 668)
                .getAndPrint()
    }

    @Test
    fun `Get all projects after call to real TeamCity API`() {
        teamCityApi
                .getProjects()
                .getAndPrint()
    }

    @Test(expected = InvalidCredentialsException::class)
    fun `Expect proper exception after call to API with invalid credentials`() {
        whenever(loginRepository.authData).thenReturn(AuthData(ADDRESS, INVALID_CREDENTIALS))
        teamCityApi
                .getBuilds()
                .getAndPrint()
    }

    fun <T> Single<T>.getAndPrint() = println(blockingGet())
}