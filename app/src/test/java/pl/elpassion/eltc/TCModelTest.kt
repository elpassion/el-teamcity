@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc

import io.reactivex.observers.TestObserver
import org.junit.Test

class TCModelTest {

    @Test
    fun `Emit NoCredentials at the beginning`() {
        val model = TCModel()
        val observer = TestObserver<AppState>()

        model.state.subscribe(observer)

        observer.assertValue(NoCredentials)
    }

}