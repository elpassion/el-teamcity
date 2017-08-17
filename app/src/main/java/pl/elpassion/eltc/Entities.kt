package pl.elpassion.eltc

import android.annotation.SuppressLint
import io.mironov.smuggler.AutoParcelable
import java.util.*

@SuppressLint("ParcelCreator")
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
) : AutoParcelable

@SuppressLint("ParcelCreator")
data class BuildType(
        val id: String,
        val name: String,
        val projectName: String
) : AutoParcelable

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
) : Comparable<TestDetails> {

    val isIgnored get() = status == Status.UNKNOWN
    val isPassed get() = status == Status.SUCCESS
    val isFailed get() = status == Status.FAILURE

    private val order get() = TestOrder.valueOf(status).order

    override fun compareTo(other: TestDetails): Int = order.compareTo(other.order)

    private enum class TestOrder(val order: Int) {
        FAILURE(0), UNKNOWN(1), SUCCESS(2)
    }
}

data class Project(
        val id: String,
        val name: String,
        val href: String
)

object Status {
    const val UNKNOWN = "UNKNOWN"
    const val SUCCESS = "SUCCESS"
    const val FAILURE = "FAILURE"
}

object State {
    const val QUEUED = "queued"
    const val RUNNING = "running"
    const val FINISHED = "finished"
}