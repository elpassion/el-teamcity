@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import org.junit.Ignore
import org.junit.Test

class TCRealApiTest {

    @Ignore
    @Test
    fun `Sample call to real TeamCity API`() {
        TCApiImpl.getBuilds("Basic dXNlcjpwYXNz").subscribe( { println(it) }, { println(it.message) })
    }
}