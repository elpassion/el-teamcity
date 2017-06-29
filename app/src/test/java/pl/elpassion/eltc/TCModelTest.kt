@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import io.reactivex.observers.TestObserver
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TCModelTest {

    val model = TCModel()
    val observer = TestObserver<AppState>()

    @Before
    fun setup() {
        model.state.subscribe(observer)
    }

    @Test
    fun `Emit NoCredentials at the beginning`() {
        observer.assertValue(NoCredentials)
    }

    @Test
    fun `Display correct error on submitting unknown host`() {
        model.perform(SubmitCredentials("invalid", "user", "pass"))
        observer.assertLastValueThat { it is UnknownHost }
    }


    private fun <T> TestObserver<T>.assertLastValueThat(predicate: (T) -> Boolean) {
        assertTrue(predicate(values().last()))
    }
}