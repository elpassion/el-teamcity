package pl.elpassion.eltc.api

import io.reactivex.Single
import pl.elpassion.eltc.Build
import pl.elpassion.eltc.Change
import pl.elpassion.eltc.Project
import pl.elpassion.eltc.TestDetails
import pl.elpassion.eltc.util.zipSingles
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

interface TeamCityApi {
    var credentials: String
    fun setAddress(url: String)
    fun getBuilds(): Single<List<Build>>
    fun getQueuedBuilds(): Single<List<Build>>
    fun getFinishedBuilds(afterDate: Date): Single<List<Build>>
    fun getBuildsForProjects(projectIds: List<String>): Single<List<Build>>
    fun getBuild(id: Int): Single<Build>
    fun getChanges(buildId: Int): Single<List<Change>>
    fun getTests(buildId: Int): Single<List<TestDetails>>
    fun getProjects(): Single<List<Project>>
}

object TeamCityApiImpl : TeamCityApi {

    override var credentials: String by Delegates.notNull()

    private var service by Delegates.notNull<Service>()

    private const val BUILDS_LOCATOR = "branch:default:any,running:any"
    private const val TESTS_LOCATOR = "count:1000"
    private const val UNAUTHORIZED_ERROR = 401

    override fun setAddress(url: String) {
        service = newRetrofit(url).create(Service::class.java)
    }

    override fun getBuilds(): Single<List<Build>> =
            service.getBuilds(credentials, BUILDS_LOCATOR).mapApiErrors().map(BuildsResponse::build)

    override fun getQueuedBuilds(): Single<List<Build>> =
            service.getQueuedBuilds(credentials).mapApiErrors().map(BuildsResponse::build)

    override fun getFinishedBuilds(afterDate: Date): Single<List<Build>> =
            service.getFinishedBuilds(credentials, "finishDate:(date:${afterDate.format()},condition:after)").mapApiErrors().map(BuildsResponse::build)

    override fun getBuildsForProjects(projectIds: List<String>): Single<List<Build>> =
            zipSingles(projectIds.map { getBuildsForProject(it) }, sortDescBy = { it.queuedDate })

    private fun getBuildsForProject(projectId: String) =
            service.getBuilds(credentials, "project:(id:$projectId),$BUILDS_LOCATOR").mapApiErrors().map(BuildsResponse::build)

    override fun getBuild(id: Int): Single<Build> =
            service.getBuild(credentials, id).mapApiErrors()

    override fun getChanges(buildId: Int): Single<List<Change>> =
            service.getChanges(credentials, "build:(id:$buildId)").mapApiErrors().map(ChangesResponse::change)

    override fun getTests(buildId: Int): Single<List<TestDetails>> =
            service.getTests(credentials, "build:(id:$buildId),$TESTS_LOCATOR").mapApiErrors().map { it.testOccurrence ?: emptyList() }

    override fun getProjects(): Single<List<Project>> =
            service.getProjects(credentials).mapApiErrors().map(ProjectsResponse::project)

    private fun <T> Single<T>.mapApiErrors() = onErrorResumeNext {
        Single.error(when {
            it is HttpException && it.code() == UNAUTHORIZED_ERROR -> InvalidCredentialsException
            it is IOException -> NetworkTimeoutException
            else -> it
        })
    }

    private interface Service {

        @GET("httpAuth/app/rest/builds?fields=build($BUILD_FIELDS)")
        fun getBuilds(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<BuildsResponse>

        @GET("httpAuth/app/rest/buildQueue?fields=build($BUILD_FIELDS)")
        fun getQueuedBuilds(@Header("Authorization") credentials: String): Single<BuildsResponse>

        @GET("httpAuth/app/rest/builds?fields=build($BUILD_FIELDS)")
        fun getFinishedBuilds(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<BuildsResponse>

        @GET("httpAuth/app/rest/builds/id:{id}?fields=$BUILD_FIELDS")
        fun getBuild(@Header("Authorization") credentials: String, @Path("id") id: Int): Single<Build>

        @GET("httpAuth/app/rest/changes?fields=change($CHANGES_FIELDS)")
        fun getChanges(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<ChangesResponse>

        @GET("httpAuth/app/rest/testOccurrences")
        fun getTests(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<TestsResponse>

        @GET("httpAuth/app/rest/projects?fields=project($PROJECT_FIELDS)")
        fun getProjects(@Header("Authorization") credentials: String): Single<ProjectsResponse>

        companion object {
            const val BUILD_FIELDS = "id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate,buildType(id,name,projectName)"
            const val CHANGES_FIELDS = "id,version,username,date,webUrl,comment"
            const val PROJECT_FIELDS = "id,name,href"
        }
    }
}

private fun Date.format() = SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.US).format(this)

data class BuildsResponse(val build: List<Build>)

data class ChangesResponse(val change: List<Change>)

data class TestsResponse(val testOccurrence: List<TestDetails>?)

data class ProjectsResponse(val project: List<Project>)