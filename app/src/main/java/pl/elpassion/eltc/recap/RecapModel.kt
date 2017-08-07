package pl.elpassion.eltc.recap

import java.util.*

class RecapModel(private val repository: RecapRepository) {

    fun onStart() {
        if (repository.lastFinishDate == null) {
            repository.lastFinishDate = Date()
        }
    }
}