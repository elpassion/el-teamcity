package pl.elpassion.eltc

import java.util.*

data class Build(
        val id: Int,
        val number: Int?,
        val status: String?,
        val state: String,
        val branchName: String?,
        val webUrl: String,
        val statusText: String?,
        val queuedDate: Date,
        val startDate: Date?,
        val finishDate: Date?,
        val buildType: BuildType
)

data class BuildType(
        val id: String,
        val name: String,
        val projectName: String
)

data class Test(
        val id: String,
        val name: String,
        val status: String,
        val duration: Int?,
        val href: String
)

data class Project(
        val id: String,
        val name: String,
        val href: String
)