@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.recap

import com.nhaarman.mockito_kotlin.*
import org.junit.Test
import pl.elpassion.eltc.api.TeamCityApi
import java.util.*

class RecapModelTest {

    private val repository = mock<RecapRepository>()
    private val api = mock<TeamCityApi>()
    private val model = RecapModel(repository, api)

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

    @Test
    fun `Call api to get finished build after last finish date`() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2017)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        whenever(repository.lastFinishDate).thenReturn(calendar.time)
        model.onStart()
        verify(api).getFinishedBuilds(calendar.time)
    }
}