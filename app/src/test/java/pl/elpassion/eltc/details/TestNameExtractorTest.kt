@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.details

import org.junit.Assert.assertEquals
import org.junit.Test

class TestNameExtractorTest {

    @Test
    fun `Extract test suite and name from junit test name`() {
        val fullName = "pl.elpassion.eltc.SampleTest.Display something to user"
        with(TestNameExtractor.extract(fullName)) {
            assertEquals("pl.elpassion.eltc.SampleTest", suite)
            assertEquals("Display something to user", name)
        }
    }
}