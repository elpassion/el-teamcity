package pl.elpassion.eltc.details

data class TestsSection(val name: String,
                        val count: Int,
                        var isExpanded: Boolean = true)