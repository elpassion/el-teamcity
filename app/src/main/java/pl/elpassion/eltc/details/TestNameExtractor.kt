package pl.elpassion.eltc.details

object TestNameExtractor {

    fun extract(fullName: String): Result {
        val name = fullName.takeLastWhile { it != '.' }.replace(oldChar = '_', newChar = ' ')
        val suite = fullName.take(fullName.count() - name.count()).removeSuffix(".")
        return Result(suite, name)
    }

    data class Result(val suite: String, val name: String)
}