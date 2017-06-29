package pl.elpassion.eltc

import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Rfc3339DateJsonAdapter
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import java.util.*

interface TCApi {
    fun getBuilds(credentials: String): Single<List<Build>>
    fun getBuild(credentials: String, id: Int): Single<Build>
}

object TCApiImpl : TCApi {

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
            service.getBuilds(credentials).map(BuildsResponse::build)

    override fun getBuild(credentials: String, id: Int): Single<Build> =
            service.getBuild(credentials, id)

    private interface Service {

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/builds?fields=build(id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate)")
        fun getBuilds(@Header("Authorization") credentials: String): Single<BuildsResponse>

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/builds/id:{id}?fields=id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate")
        fun getBuild(@Header("Authorization") credentials: String, @Path("id") id: Int): Single<Build>
    }
}

private data class BuildsResponse(val build: List<Build>)

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
        val finishDate: Date
)