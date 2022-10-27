package com.kcteam.features.report.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.report.model.MISResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Pratishruti on 22-12-2017.
 */
interface GetMISApi {
    @FormUrlEncoded
    @POST("MISDetails/List")
    fun getMISDetail(@Field("user_id") user_id: String, @Field("session_token") session_token: String, @Field("month") month: String,
                     @Field("start_date") start_date: String, @Field("end_date") end_date: String, @Field("year") year: String): Observable<MISResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): GetMISApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(GetMISApi::class.java)
        }
    }
}