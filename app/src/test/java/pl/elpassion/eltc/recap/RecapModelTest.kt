@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.recap

import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Test
import pl.elpassion.eltc.api.TeamCityApi
import pl.elpassion.eltc.createBuild
import pl.elpassion.eltc.thenJust
import pl.elpassion.eltc.thenNever
import java.util.*

class RecapModelTest {

    private val repository = mock<RecapRepository>()
    private val api = mock<TeamCityApi>()
    private val model = RecapModel(repository, api)

    @Before
    fun setup() {
        whenever(api.getFinishedBuilds(any())).thenNever()
    }

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
    fun `Call api to get finished builds after last finish date`() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2017)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        whenever(repository.lastFinishDate).thenReturn(calendar.time)
        model.onStart()
        verify(api).getFinishedBuilds(calendar.time)
    }

    @Test
    fun `Not call api to get finished builds on first start`() {
        whenever(repository.lastFinishDate).thenReturn(null)
        model.onStart()
        verify(api, never()).getFinishedBuilds(any())
    }

    @Test
    fun `Update last finish date with new value from finished builds on api result`() {
        val lastFinishDate = Date(1502103373000)
        val newFinishDate = Date(1502103410000)
        whenever(repository.lastFinishDate).thenReturn(lastFinishDate)
        whenever(api.getFinishedBuilds(lastFinishDate)).thenJust(listOf(
                createBuild(finishDate = newFinishDate)))
        model.onStart()
        verify(repository).lastFinishDate = newFinishDate
    }

    @Test
    fun `Update last finish date with max value from finished builds on api result`() {
        val lastFinishDate = Date(1502103373000)
        val newFinishDates = listOf(Date(1502103410000), Date(1502103410002), Date(1502103410001))
        whenever(repository.lastFinishDate).thenReturn(lastFinishDate)
        whenever(api.getFinishedBuilds(lastFinishDate)).thenJust(listOf(
                createBuild(finishDate = newFinishDates[0]),
                createBuild(finishDate = newFinishDates[1]),
                createBuild(finishDate = newFinishDates[2])))
        model.onStart()
        verify(repository).lastFinishDate = newFinishDates[1]
    }
}