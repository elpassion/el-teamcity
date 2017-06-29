package pl.elpassion.eltc

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface TCApi {
    fun getBuilds(credentials: String): Single<List<Build>>
}

object TCApiImpl : TCApi {

    private val URL = "http://192.168.1.155:8111"
    private val retrofit = Retrofit.Builder().baseUrl(URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    private val service = retrofit.create(Service::class.java)

    override fun getBuilds(credentials: String): Single<List<Build>> =
            service.getBuilds(credentials).map(BuildsResponse::build)

    private interface Service {

        @Headers("Accept: application/json")
        @GET("httpAuth/app/rest/builds?fields=build(id,number,status,state,branchName,webUrl,statusText,queuedDate,startDate,finishDate)")
        fun getBuilds(@Header("Authorization") credentials: String): Single<BuildsResponse>
    }
}

private data class BuildsResponse(val build: List<Build>)

data class Build(
        val id: Int,
        val number: Int,
        val status: String,
        val state: String,
        val branchName: String,
        val webUrl: String,
        val statusText: String,
        val queuedDate: String,
        val startDate: String,
        val finishDate: String
)