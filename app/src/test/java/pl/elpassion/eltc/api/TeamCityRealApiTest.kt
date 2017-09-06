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
import java.util.*

@Ignore
class TeamCityRealApiTest {

    private val ADDRESS = "http://192.168.1.155:8111"
    private val CREDENTIALS = "Basic dXNlcjpwYXNz"
    private val INVALID_CREDENTIALS = "invalid credentials"
    private val loginRepository = mock<LoginRepository>()
    private val teamCityApi = TeamCityApiImpl(loginRepository)

    @Before
    fun setup() {
        teamCityApi.credentials = CREDENTIALS
        whenever(loginRepository.authData).thenReturn(AuthData(ADDRESS, CREDENTIALS))
    }

    @Test
    fun `Get builds after call to real TeamCity API`() {
        teamCityApi
                .getBuilds()
                .getAndPrint()
    }

    @Test
    fun `Get queued builds after call to real TeamCity API`() {
        teamCityApi
                .getQueuedBuilds()
                .getAndPrint()
    }

    @Test
    fun `Get finished builds after specified date after call to real TeamCity API`() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2017)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        teamCityApi
                .getFinishedBuilds(afterDate = calendar.time)
                .getAndPrint()
    }

    @Test
    fun `Get finished builds of the specified project after call to real TeamCity API`() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2017)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        teamCityApi
                .getFinishedBuildsForProjects(
                        projectIds = listOf("TeamcityAndroidClient"),
                        afterDate = calendar.time)
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
    fun `Get changes included into the build after call to real TeamCity API`() {
        teamCityApi
                .getChanges(buildId = 668)
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

    @Test
    fun `Get problem occurrences for failed build`() {
        teamCityApi
                .getProblemOccurrences(buildId = 1958)
                .getAndPrint()
    }

    @Test(expected = InvalidCredentialsException::class)
    fun `Expect proper exception after call to API with invalid credentials`() {
        teamCityApi.credentials = INVALID_CREDENTIALS
        teamCityApi
                .getBuilds()
                .getAndPrint()
    }

    fun <T> Single<T>.getAndPrint() = println(blockingGet())
}