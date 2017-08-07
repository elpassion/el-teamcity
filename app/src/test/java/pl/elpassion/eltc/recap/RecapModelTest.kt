@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.recap

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import java.util.*

class RecapModelTest {

    private val repository = mock<RecapRepository>()
    private val model = RecapModel(repository)

    @Test
    fun `Set last finish date to initial date on first start`() {
        whenever(repository.lastFinishDate).thenReturn(null)
        model.onStart()
        verify(repository).lastFinishDate = any()
    }

    @Test
    fun `Not set last finish date to new date on subsequent start`() {
        whenever(repository.lastFinishDate).thenReturn(Date())
        model.onStart()
        verify(repository, never()).lastFinishDate = any()
    }
}