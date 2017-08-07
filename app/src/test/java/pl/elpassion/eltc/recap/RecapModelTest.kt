@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.recap

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Test

class RecapModelTest {

    private val repository = mock<RecapRepository>()
    private val model = RecapModel(repository)

    @Test
    fun `Set last finish date to initial date on first start`() {
        whenever(repository.lastFinishDate).thenReturn(null)
        model.onStart()
        verify(repository).lastFinishDate = any()
    }
}