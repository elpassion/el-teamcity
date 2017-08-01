@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.details

import org.junit.Assert.assertEquals
import org.junit.Test

class TestNameExtractorTest {

    @Test
    fun `Extract test suite and case from junit test name`() {
        val fullName = "pl.elpassion.eltc.SampleTest.Display something to user"
        with(TestNameExtractor.extract(fullName)) {
            assertEquals("pl.elpassion.eltc.SampleTest", suite)
            assertEquals("Display something to user", case)
        }
    }

    @Test
    fun `Extract test suite and case from android test name`() {
        val fullName = "pl.elpassion.eltc.SampleTest.Display_something_to_user"
        with(TestNameExtractor.extract(fullName)) {
            assertEquals("pl.elpassion.eltc.SampleTest", suite)
            assertEquals("Display something to user", case)
        }
    }

    @Test
    fun `Extract test suite and case from test name in camel case`() {
        val fullName = "pl.elpassion.eltc.camel.CamelCaseTest.shouldFormatIt"
        with(TestNameExtractor.extract(fullName)) {
            assertEquals("pl.elpassion.eltc.camel.CamelCaseTest", suite)
            assertEquals("Should format it", case)
        }
    }
}