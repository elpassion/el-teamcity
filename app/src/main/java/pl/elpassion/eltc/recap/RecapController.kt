package pl.elpassion.eltc.recap

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import pl.elpassion.eltc.Build
import pl.elpassion.eltc.Status
import pl.elpassion.eltc.api.TeamCityApi
import pl.elpassion.eltc.login.LoginRepository
import pl.elpassion.eltc.util.SchedulersSupplier
import pl.elpassion.eltc.util.log
import java.util.*

class RecapController(private val loginRepository: LoginRepository,
                      private val recapRepository: RecapRepository,
                      private val projectsIds: List<String>?,
                      private val api: TeamCityApi,
                      private val notifier: RecapNotifier,
                      private val onFinish: () -> Unit,
                      private val schedulers: SchedulersSupplier) {

    private val compositeDisposable = CompositeDisposable()

    fun onStart() {
        val lastFinishDate = recapRepository.lastFinishDate
        if (lastFinishDate == null) {
            recapRepository.lastFinishDate = Date()
            onFinish()
        } else {
            tryToFetchData(lastFinishDate)
        }
    }

    fun onStop() {
        compositeDisposable.clear()
    }

    private fun tryToFetchData(lastFinishDate: Date) {
        val authData = loginRepository.authData
        if (authData != null) {
            getFinishedBuilds(lastFinishDate)
        } else {
            onFinish()
        }
    }

    private fun getFinishedBuilds(lastFinishDate: Date) {
        getCallForFinishedBuilds(lastFinishDate, projectsIds)
                .subscribeOn(schedulers.subscribeOn)
                .observeOn(schedulers.observeOn)
                .subscribe(onFinishedBuilds, onError)
                .addTo(compositeDisposable)
    }

    private fun getCallForFinishedBuilds(lastFinishDate: Date, projectsIds: List<String>?) =
            if (projectsIds != null) {
                api.getFinishedBuildsForProjects(lastFinishDate, projectsIds)
            } else {
                api.getFinishedBuilds(lastFinishDate)
            }

    private val onFinishedBuilds: (List<Build>) -> Unit = { builds ->
        log("new builds: ${builds.count()}")
        val finishDate = builds.lastFinishDate
        if (finishDate != null) {
            notifyAboutFailures(builds)
            recapRepository.lastFinishDate = finishDate
        }
        onFinish()
    }

    private val onError: (Throwable) -> Unit = { onFinish() }

    private val List<Build>.lastFinishDate get() = finishDates.maxBy { it.time }

    private val List<Build>.finishDates get() = map { it.finishDate }.filterNotNull()

    private fun notifyAboutFailures(builds: List<Build>) {
        val failedBuilds = builds.filter { it.status == Status.FAILURE }
        if (failedBuilds.isNotEmpty()) {
            notifier.showFailureNotifications(failedBuilds)
        }
    }
}