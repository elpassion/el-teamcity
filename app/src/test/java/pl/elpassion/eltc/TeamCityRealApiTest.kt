@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import org.junit.Ignore
import org.junit.Test

class TeamCityRealApiTest {

    @Ignore
    @Test
    fun `Get builds call to real TeamCity API`() {
        TeamCityApiImpl.getBuilds("Basic dXNlcjpwYXNz")
                .subscribe({
                    println(it.joinToString { "\n$it" })
                }, {
                    println(it.message)
                })
    }

    @Ignore
    @Test
    fun `Get single build call to real TeamCity API`() {
        TeamCityApiImpl.getBuild("Basic dXNlcjpwYXNz", 1)
                .subscribe({
                    println(it)
                }, {
                    println(it.message)
                })
    }
}