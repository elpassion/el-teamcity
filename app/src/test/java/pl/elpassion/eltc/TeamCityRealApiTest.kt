@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import io.reactivex.Single
import org.junit.Ignore
import org.junit.Test

@Ignore
class TeamCityRealApiTest {

    private val CREDENTIALS = "dXNlcjpwYXNz"
    private val INVALID_CREDENTIALS = "invalid credentials"

    @Test
    fun `Get builds after call to real TeamCity API`() {
        TeamCityApiImpl
                .getBuilds(CREDENTIALS)
                .getAndPrint()
    }

    @Test
    fun `Get builds of the specified project after call to real TeamCity API`() {
        TeamCityApiImpl
                .getBuildsForProject(CREDENTIALS, projectId = "TeamcityAndroidClient")
                .getAndPrint()
    }

    @Test
    fun `Get single build after call to real TeamCity API`() {
        TeamCityApiImpl
                .getBuild(CREDENTIALS, id = 1)
                .getAndPrint()
    }

    @Test
    fun `Get all tests per build after call to real TeamCity API`() {
        TeamCityApiImpl
                .getTests(CREDENTIALS, buildId = 668)
                .getAndPrint()
    }

    @Test
    fun `Get all projects after call to real TeamCity API`() {
        TeamCityApiImpl
                .getProjects(CREDENTIALS)
                .getAndPrint()
    }

    @Test(expected = InvalidCredentialsException::class)
    fun `Expect proper exception after call to API with invalid credentials`() {
        TeamCityApiImpl
                .getBuilds(INVALID_CREDENTIALS)
                .getAndPrint()
    }

    fun <T> Single<T>.getAndPrint() = println(blockingGet())
}