package pl.elpassion.eltc.api

import io.reactivex.Single
import pl.elpassion.eltc.Build
import pl.elpassion.eltc.Project
import pl.elpassion.eltc.Test
import pl.elpassion.eltc.login.LoginRepository
import retrofit2.HttpException
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.io.IOException

interface TeamCityApi {
    fun getBuilds(): Single<List<Build>>
    fun getBuildsForProjects(projectIds: List<String>): Single<List<Build>>
    fun getBuild(id: Int): Single<Build>
    fun getTests(buildId: Int): Single<List<Test>>
    fun getProjects(): Single<List<Project>>
}

class TeamCityApiImpl(private val loginRepository: LoginRepository) : TeamCityApi {

    private val URL = "http://192.168.1.155:8111"
    private val credentials get() = "Basic ${loginRepository.authData?.credentials}"
    private var retrofit = createRetrofit(URL)
    private val service = retrofit.create(Service::class.java)

    override fun getBuilds(): Single<List<Build>> =
            service.getBuilds(credentials).mapApiErrors().map(BuildsResponse::build)

    override fun getBuildsForProjects(projectIds: List<String>): Single<List<Build>> =
            Single.zip<List<Build>, List<Build>>(projectIds.map {
                getBuildsForProject(it)
            }, {
                it.map { it as List<Build> }.flatten().sortedByDescending { it.finishDate }
            })

    private fun getBuildsForProject(projectId: String) =
            service.getBuilds(credentials, "project:(id:$projectId)").mapApiErrors().map(BuildsResponse::build)

    override fun getBuild(id: Int): Single<Build> =
            service.getBuild(credentials, id).mapApiErrors()

    override fun getTests(buildId: Int): Single<List<Test>> =
            service.getTests(credentials, "build:(id:$buildId)").mapApiErrors().map(TestsResponse::testOccurrence)

    override fun getProjects(): Single<List<Project>> =
            service.getProjects(credentials).mapApiErrors().map(ProjectsResponse::project)

    private fun <T> Single<T>.mapApiErrors() = onErrorResumeNext {
        Single.error(when {
            it is HttpException && it.code() == 401 -> InvalidCredentialsException // FIXME: this condition is not precise enough
            it is IOException -> NetworkTimeoutException
            else -> it
        })
    }

    private interface Service {

        @GET("httpAuth/app/rest/builds?fields=build($BUILD_FIELDS)")
        fun getBuilds(@Header("Authorization") credentials: String): Single<BuildsResponse>

        @GET("httpAuth/app/rest/builds?fields=build($BUILD_FIELDS)")
        fun getBuilds(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<BuildsResponse>

        @GET("httpAuth/app/rest/builds/id:{id}?fields=$BUILD_FIELDS")
        fun getBuild(@Header("Authorization") credentials: String, @Path("id") id: Int): Single<Build>

        @GET("httpAuth/app/rest/testOccurrences")
        fun getTests(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<TestsResponse>

        @GET("httpAuth/app/rest/projects?fields=project($PROJECT_FIELDS)")
        fun getProjects(@Header("Authorization") credentials: String): Single<ProjectsResponse>

        companion object {
            const val BUILD_FIELDS = "id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate,buildType(id,name,projectName)"
            const val PROJECT_FIELDS = "id,name,href"
        }
    }
}

data class BuildsResponse(val build: List<Build>)

data class TestsResponse(val testOccurrence: List<Test>)

data class ProjectsResponse(val project: List<Project>)