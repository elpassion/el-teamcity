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
        repository.lastFinishDate = builds.map { it.finishDate }.filterNotNull()
                .maxBy { it.time }
    }

    private val onError: (Throwable) -> Unit = { }
}