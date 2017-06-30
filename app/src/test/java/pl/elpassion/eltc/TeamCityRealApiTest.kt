@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import io.reactivex.Single
import org.junit.Ignore
import org.junit.Test

@Ignore
class TeamCityRealApiTest {

    private val CREDENTIALS = "dXNlcjpwYXNz"

    @Test
    fun `Get builds after call to real TeamCity API`() {
        TeamCityApiImpl
                .getBuilds(CREDENTIALS)
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

    fun <T> Single<T>.getAndPrint() = println(blockingGet())
}