@file:Suppress("IllegalIdentifier")

package pl.elpassion.eltc.recap

import com.nhaarman.mockito_kotlin.*
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers.trampoline
import org.junit.Before
import org.junit.Test
import pl.elpassion.eltc.api.TeamCityApi
import pl.elpassion.eltc.createBuild
import pl.elpassion.eltc.thenError
import pl.elpassion.eltc.thenJust
import pl.elpassion.eltc.thenNever
import pl.elpassion.eltc.util.SchedulersSupplier
import java.util.*

class RecapModelTest {

    private val repository = mock<RecapRepository>()
    private val api = mock<TeamCityApi>()
    private val notifier = mock<RecapNotifier>()
    private val onFinish = mock<() -> Unit>()

    @Before
    fun setup() {
        whenever(api.getFinishedBuilds(any())).thenNever()
    }

    @Test
    fun `Set last finish date to initial date on first start`() {
        whenever(repository.lastFinishDate).thenReturn(null)
        createModel().onStart()
        verify(repository).lastFinishDate = any()
    }

    @Test
    fun `Do not set last finish date to new date on subsequent start`() {
        whenever(repository.lastFinishDate).thenReturn(Date())
        createModel().onStart()
        verify(repository, never()).lastFinishDate = anyOrNull()
    }

    @Test
    fun `Call api to get finished builds after last finish date`() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2017)
            set(Calendar.MONTH, Calendar.AUGUST)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        whenever(repository.lastFinishDate).thenReturn(calendar.time)
        createModel().onStart()
        verify(api).getFinishedBuilds(calendar.time)
    }

    @Test
    fun `Do not call api to get finished builds on first start`() {
        whenever(repository.lastFinishDate).thenReturn(null)
        createModel().onStart()
        verify(api, never()).getFinishedBuilds(any())
    }

    @Test
    fun `Update last finish date with new value from finished builds on api result`() {
        val lastFinishDate = Date(1502103373000)
        val newFinishDate = Date(1502103410000)
        whenever(repository.lastFinishDate).thenReturn(lastFinishDate)
        whenever(api.getFinishedBuilds(lastFinishDate)).thenJust(listOf(
                createBuild(finishDate = newFinishDate)))
        createModel().onStart()
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
        createModel().onStart()
        verify(repository).lastFinishDate = newFinishDates[1]
    }

    @Test
    fun `Do not update last finish date on api error`() {
        val lastFinishDate = Date(1502103373000)
        whenever(repository.lastFinishDate).thenReturn(lastFinishDate)
        whenever(api.getFinishedBuilds(lastFinishDate)).thenError(RuntimeException())
        createModel().onStart()
        verify(repository, never()).lastFinishDate = anyOrNull()
    }

    @Test
    fun `Do not update last finish date on empty result`() {
        val lastFinishDate = Date(1502103373000)
        whenever(repository.lastFinishDate).thenReturn(lastFinishDate)
        whenever(api.getFinishedBuilds(lastFinishDate)).thenJust(listOf(createBuild(finishDate = null)))
        createModel().onStart()
        verify(repository, never()).lastFinishDate = anyOrNull()
    }

    @Test
    fun `Show notification with failed builds on api result`() {
        val successfulBuild = createBuild(finishDate = Date(1502103410000), status = "SUCCESS")
        val failedBuild = createBuild(finishDate = Date(1502103410001), status = "FAILURE")
        whenever(repository.lastFinishDate).thenReturn(Date(1502103373000))
        whenever(api.getFinishedBuilds(Date(1502103373000))).thenJust(listOf(successfulBuild, failedBuild))
        createModel().onStart()
        verify(notifier).showFailureNotification(listOf(failedBuild))
    }

    @Test
    fun `Do not show notification when no failures on api result`() {
        whenever(repository.lastFinishDate).thenReturn(Date(1502103373000))
        whenever(api.getFinishedBuilds(Date(1502103373000))).thenJust(listOf(
                createBuild(finishDate = Date(1502103410000), status = "SUCCESS")))
        createModel().onStart()
        verify(notifier, never()).showFailureNotification(any())
    }

    @Test
    fun `Invoke finish on first start`() {
        whenever(repository.lastFinishDate).thenReturn(null)
        createModel().onStart()
        verify(onFinish).invoke()
    }

    @Test
    fun `Do not invoke finish on subsequent start before result from api`() {
        val lastFinishDate = Date(1502103373000)
        whenever(repository.lastFinishDate).thenReturn(lastFinishDate)
        createModel().onStart()
        verify(onFinish, never()).invoke()
    }

    @Test
    fun `Invoke finish on api error`() {
        val lastFinishDate = Date(1502103373000)
        whenever(repository.lastFinishDate).thenReturn(lastFinishDate)
        whenever(api.getFinishedBuilds(lastFinishDate)).thenError(RuntimeException())
        createModel().onStart()
        verify(onFinish).invoke()
    }

    @Test
    fun `Invoke finish on successful api result`() {
        whenever(repository.lastFinishDate).thenReturn(Date(1502103373000))
        whenever(api.getFinishedBuilds(Date(1502103373000))).thenJust(listOf(
                createBuild(finishDate = Date(1502103410000))))
        createModel().onStart()
        verify(onFinish).invoke()
    }

    private fun createModel(subscribeOnScheduler: Scheduler = trampoline(),
                            observeOnScheduler: Scheduler = trampoline()) =
            RecapModel(repository, api, notifier, onFinish,
                    SchedulersSupplier(subscribeOnScheduler, observeOnScheduler))
}