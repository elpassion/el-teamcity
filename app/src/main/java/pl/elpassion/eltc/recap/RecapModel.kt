package pl.elpassion.eltc.recap

import pl.elpassion.eltc.Build
import pl.elpassion.eltc.api.TeamCityApi
import java.util.*

class RecapModel(private val repository: RecapRepository,
                 private val api: TeamCityApi,
                 private val notifier: RecapNotifier) {

    fun onStart() {
        val lastFinishDate = repository.lastFinishDate
        if (lastFinishDate == null) {
            repository.lastFinishDate = Date()
        } else {
            getFinishedBuilds(lastFinishDate)
        }
    }

    private fun getFinishedBuilds(lastFinishDate: Date) {
        api.getFinishedBuilds(lastFinishDate)
                .subscribe(onFinishedBuilds, onError)
    }

    private val onFinishedBuilds: (List<Build>) -> Unit = { builds ->
        val finishDate = builds.lastFinishDate
        if (finishDate != null) {
            notifyAboutFailures(builds)
            repository.lastFinishDate = finishDate
        }
    }

    private val onError: (Throwable) -> Unit = { }

    private val List<Build>.lastFinishDate get() = finishDates.maxBy { it.time }

    private val List<Build>.finishDates get() = map { it.finishDate }.filterNotNull()

    private fun notifyAboutFailures(builds: List<Build>) {
        val failedBuilds = builds.filter { it.status == "FAILURE" }
        notifier.showFailureNotification(failedBuilds)
    }
}