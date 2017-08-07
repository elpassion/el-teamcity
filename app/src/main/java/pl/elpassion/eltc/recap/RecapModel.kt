package pl.elpassion.eltc.recap

import pl.elpassion.eltc.Build
import pl.elpassion.eltc.api.TeamCityApi
import java.util.*

class RecapModel(private val repository: RecapRepository,
                 private val api: TeamCityApi) {

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
            repository.lastFinishDate = finishDate
        }
    }

    private val List<Build>.lastFinishDate get() = finishDates.maxBy { it.time }

    private val List<Build>.finishDates get() = map { it.finishDate }.filterNotNull()

    private val onError: (Throwable) -> Unit = { }
}