package pl.elpassion.eltc.api

import io.reactivex.Single
import pl.elpassion.eltc.*
import pl.elpassion.eltc.login.LoginRepository
import pl.elpassion.eltc.util.zipSingles
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

interface TeamCityApi {
    fun getBuilds(): Single<List<Build>>
    fun getQueuedBuilds(): Single<List<Build>>
    fun getFinishedBuilds(afterDate: Date): Single<List<Build>>
    fun getFinishedBuildsForProjects(afterDate: Date, projectIds: List<String>): Single<List<Build>>
    fun getBuildsForProjects(projectIds: List<String>): Single<List<Build>>
    fun getBuild(id: Int): Single<Build>
    fun getChanges(buildId: Int): Single<List<Change>>
    fun getTests(buildId: Int): Single<List<TestDetails>>
    fun getProjects(): Single<List<Project>>
    fun getProblemOccurrences(buildId: Int): Single<List<ProblemOccurrence>>
}

class TeamCityApiImpl(private val loginRepository: LoginRepository) : TeamCityApi {

    private var baseUrl: String? = null
    private var endpoint: Service? = null
    private val service: Service
        get() {
            val authData = loginRepository.authData
            if (authData != null) {
                if (authData.address != baseUrl) {
                    baseUrl = authData.address
                    endpoint = with(authData) {
                        newRetrofit(address, fullCredentials).create(Service::class.java)
                    }
                }
                return endpoint!!
            } else throw IllegalStateException("Server url not available")
        }

    override fun getBuilds(): Single<List<Build>> =
            service.getBuilds(BUILDS_LOCATOR).mapApiErrors().map(BuildsResponse::build)

    override fun getQueuedBuilds(): Single<List<Build>> =
            service.getQueuedBuilds().mapApiErrors().map(BuildsResponse::build)

    override fun getFinishedBuilds(afterDate: Date): Single<List<Build>> =
            service.getFinishedBuilds("finishDate:(date:${afterDate.format()},condition:after)").mapApiErrors().map(BuildsResponse::build)

    override fun getFinishedBuildsForProjects(afterDate: Date, projectIds: List<String>): Single<List<Build>> =
            zipSingles(projectIds.map { getFinishedBuildsForProject(afterDate, it) }, sortDescBy = { it.queuedDate })

    override fun getBuildsForProjects(projectIds: List<String>): Single<List<Build>> =
            zipSingles(projectIds.map { getBuildsForProject(it) }, sortDescBy = { it.queuedDate })

    private fun getFinishedBuildsForProject(afterDate: Date, projectId: String) =
            service.getBuilds("project:(id:$projectId),$BUILDS_LOCATOR,finishDate:(date:${afterDate.format()},condition:after)").mapApiErrors().map(BuildsResponse::build)

    private fun getBuildsForProject(projectId: String) =
            service.getBuilds("project:(id:$projectId),$BUILDS_LOCATOR").mapApiErrors().map(BuildsResponse::build)

    override fun getBuild(id: Int): Single<Build> =
            service.getBuild(id).mapApiErrors()

    override fun getChanges(buildId: Int): Single<List<Change>> =
            service.getChanges("build:(id:$buildId)").mapApiErrors().map(ChangesResponse::change)

    override fun getTests(buildId: Int): Single<List<TestDetails>> =
            service.getTests("build:(id:$buildId),$TESTS_LOCATOR").mapApiErrors().map { it.testOccurrence ?: emptyList() }

    override fun getProjects(): Single<List<Project>> =
            service.getProjects().mapApiErrors().map(ProjectsResponse::project)

    override fun getProblemOccurrences(buildId: Int): Single<List<ProblemOccurrence>> =
            service.getProblemOccurrences("build:(id:$buildId)").mapApiErrors().map(ProblemsResponse::problemOccurrence)

    private fun <T> Single<T>.mapApiErrors() = onErrorResumeNext {
        Single.error(when {
            it is HttpException && it.code() == UNAUTHORIZED_ERROR -> InvalidCredentialsException
            it is IOException -> NetworkTimeoutException
            else -> it
        })
    }

    private interface Service {

        @GET("httpAuth/app/rest/builds?fields=build($BUILD_FIELDS)")
        fun getBuilds(@Query("locator") locator: String): Single<BuildsResponse>

        @GET("httpAuth/app/rest/buildQueue?fields=build($BUILD_FIELDS)")
        fun getQueuedBuilds(): Single<BuildsResponse>

        @GET("httpAuth/app/rest/builds?fields=build($BUILD_FIELDS)")
        fun getFinishedBuilds(@Query("locator") locator: String): Single<BuildsResponse>

        @GET("httpAuth/app/rest/builds/id:{id}?fields=$BUILD_FIELDS")
        fun getBuild(@Path("id") id: Int): Single<Build>

        @GET("httpAuth/app/rest/changes?fields=change($CHANGES_FIELDS)")
        fun getChanges(@Query("locator") locator: String): Single<ChangesResponse>

        @GET("httpAuth/app/rest/testOccurrences")
        fun getTests(@Query("locator") locator: String): Single<TestsResponse>

        @GET("httpAuth/app/rest/projects?fields=project($PROJECT_FIELDS)")
        fun getProjects(): Single<ProjectsResponse>

        @GET("httpAuth/app/rest/problemOccurrences?fields=problemOccurrence($PROBLEM_OCCURRENCE_FIELDS)")
        fun getProblemOccurrences(@Query("locator") locator: String): Single<ProblemsResponse>

        companion object {
            const val BUILD_FIELDS = "id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate,buildType(id,name,projectName)"
            const val CHANGES_FIELDS = "id,version,username,date,webUrl,comment"
            const val PROJECT_FIELDS = "id,name,href"
            const val PROBLEM_OCCURRENCE_FIELDS = "id,type,identity,details,href"
        }
    }

    companion object {
        private const val BUILDS_LOCATOR = "branch:default:any,running:any"
        private const val TESTS_LOCATOR = "count:1000"
        private const val UNAUTHORIZED_ERROR = 401
    }
}

private fun Date.format() = SimpleDateFormat("yyyyMMdd'T'HHmmssZ", Locale.US).format(this)

data class BuildsResponse(val build: List<Build>)

data class ChangesResponse(val change: List<Change>)

data class TestsResponse(val testOccurrence: List<TestDetails>?)

data class ProjectsResponse(val project: List<Project>)

data class ProblemsResponse(val problemOccurrence: List<ProblemOccurrence>)