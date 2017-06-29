@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import org.junit.Ignore
import org.junit.Test

@Ignore
class TeamCityRealApiTest {

    private val CREDENTIALS = "Basic dXNlcjpwYXNz"

    @Test
    fun `Get builds after call to real TeamCity API`() {
        TeamCityApiImpl.getBuilds(CREDENTIALS)
                .subscribe({
                    println(it.joinToString { "\n$it" })
                }, {
                    println(it.message)
                })
    }

    @Test
    fun `Get single build after call to real TeamCity API`() {
        TeamCityApiImpl.getBuild(CREDENTIALS, id = 1)
                .subscribe({
                    println(it)
                }, {
                    println(it.message)
                })
    }

    @Test
    fun `Get all tests per build after call to real TeamCity API`() {
        TeamCityApiImpl.getTests(CREDENTIALS, buildId = 668)
                .subscribe({
                    println(it)
                }, {
                    println(it.message)
                })
    }
}