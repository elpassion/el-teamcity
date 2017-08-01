package pl.elpassion.eltc.details

object TestNameExtractor {

    fun extract(fullName: String): Result {
        val testName = fullName.takeLastWhile { it != '.' }
        val case = extractCase(testName)
        val suite = extractSuite(fullName, testName)
        return Result(suite, case)
    }

    private fun extractCase(testName: String) = testName
            .replace(oldChar = '_', newChar = ' ')
            .replace(Regex("((?!^)[A-Z])")) { " ${it.value.toLowerCase()}" }
            .capitalize()

    private fun extractSuite(fullName: String, testName: String) = fullName
            .take(fullName.count() - testName.count())
            .removeSuffix(".")

    data class Result(val suite: String, val case: String)
}