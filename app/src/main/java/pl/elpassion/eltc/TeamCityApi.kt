package pl.elpassion.eltc

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import io.reactivex.Single
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.*

interface TeamCityApi {
    fun getBuilds(credentials: String): Single<List<Build>>
    fun getBuildsForProjects(credentials: String, projectIds: List<String>): Single<List<Build>>
    fun getBuild(credentials: String, id: Int): Single<Build>
    fun getTests(credentials: String, buildId: Int): Single<List<Test>>
    fun getProjects(credentials: String): Single<List<Project>>
}

object TeamCityApiImpl : TeamCityApi {

    private val URL = "http://192.168.1.155:8111"
    private val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
            .build()
    private val retrofit = Retrofit.Builder().baseUrl(URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    private val service = retrofit.create(Service::class.java)

    override fun getBuilds(credentials: String): Single<List<Build>> =
            service.getBuilds("Basic $credentials").mapApiErrors().map(BuildsResponse::build)

    override fun getBuildsForProjects(credentials: String, projectIds: List<String>): Single<List<Build>> =
            Single.zip<List<Build>, List<Build>>(projectIds.map {
                service.getSpecificBuilds("Basic $credentials", "project:(id:$it)")
                        .mapApiErrors().map(BuildsResponse::build)
            }, {
                it.map { it as List<Build> }.flatten().sortedByDescending { it.finishDate }
            })

    override fun getBuild(credentials: String, id: Int): Single<Build> =
            service.getBuild("Basic $credentials", id).mapApiErrors()

    override fun getTests(credentials: String, buildId: Int): Single<List<Test>> =
            service.getTests("Basic $credentials", "build:(id:$buildId)").mapApiErrors().map(TestsResponse::testOccurrence)

    override fun getProjects(credentials: String): Single<List<Project>> =
            service.getProjects("Basic $credentials").mapApiErrors().map(ProjectsResponse::project)

    private fun <T> Single<T>.mapApiErrors() = onErrorResumeNext {
        Single.error(when {
            it is HttpException && it.code() == 401 -> InvalidCredentialsException // FIXME: this condition is not precise enough
            it is IOException -> NetworkTimeoutException
            else -> it
        })
    }

    private interface Service {

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/builds?fields=build(id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate,buildType(id,name,projectName))")
        fun getBuilds(@Header("Authorization") credentials: String): Single<BuildsResponse>

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/builds?fields=build(id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate,buildType(id,name,projectName))")
        fun getSpecificBuilds(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<BuildsResponse>

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/builds/id:{id}?fields=id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate,buildType(id,name,projectName)")
        fun getBuild(@Header("Authorization") credentials: String, @Path("id") id: Int): Single<Build>

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/testOccurrences")
        fun getTests(@Header("Authorization") credentials: String, @Query("locator") locator: String): Single<TestsResponse>

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/projects?fields=project(id,name,href)")
        fun getProjects(@Header("Authorization") credentials: String): Single<ProjectsResponse>
    }
}

data class BuildsResponse(val build: List<Build>)

data class Build(
        val id: Int,
        val number: Int,
        val status: String,
        val state: String,
        val branchName: String?,
        val webUrl: String,
        val statusText: String,
        val queuedDate: Date,
        val startDate: Date,
        val finishDate: Date,
        val buildType: BuildType
)

data class BuildType(
        val id: String,
        val name: String,
        val projectName: String
)

data class TestsResponse(val testOccurrence: List<Test>)

data class Test(
        val id: String,
        val name: String,
        val status: String,
        val duration: Int?,
        val href: String
)

data class ProjectsResponse(val project: List<Project>)

data class Project(
        val id: String,
        val name: String,
        val href: String
)