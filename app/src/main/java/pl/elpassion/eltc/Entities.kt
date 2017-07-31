package pl.elpassion.eltc

import java.util.*

data class Build(
        val id: Int,
        val number: String?,
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

data class Change(
        val id: String,
        val version: String,
        val username: String,
        val date: Date,
        val webUrl: String,
        val comment: String
)

data class TestDetails(
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