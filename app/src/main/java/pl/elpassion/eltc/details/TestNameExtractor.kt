package pl.elpassion.eltc.details

object TestNameExtractor {

    fun extract(fullName: String): Result {
        val name = fullName.takeLastWhile { it != '.' }
        val suite = fullName.take(fullName.count() - name.count() - 1)
        return Result(suite, name)
    }

    data class Result(val suite: String, val name: String)
}