@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import org.junit.Ignore
import org.junit.Test

class TCRealApiTest {

    @Ignore
    @Test
    fun `Get builds call to real TeamCity API`() {
        TCApiImpl.getBuilds("Basic dXNlcjpwYXNz")
                .subscribe({
                    println(it.joinToString { "\n$it" })
                }, {
                    println(it.message)
                })
    }

    @Ignore
    @Test
    fun `Get single build call to real TeamCity API`() {
        TCApiImpl.getBuild("Basic dXNlcjpwYXNz", 1)
                .subscribe({
                    println(it)
                }, {
                    println(it.message)
                })
    }
}