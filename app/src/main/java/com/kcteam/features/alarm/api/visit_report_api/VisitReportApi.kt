package com.kcteam.features.alarm.api.visit_report_api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.alarm.model.VisitReportResponseModel
import com.kcteam.features.beatCustom.BeatTeamResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 21-02-2019.
 */
interface VisitReportApi {
    @FormUrlEncoded
    @POST("AlarmConfig/FetchVist")
    fun visitReportResponse(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("from_date") from_date: String,
                                  @Field("to_date") to_date: String): Observable<VisitReportResponseModel>

    @FormUrlEncoded
    @POST("AlarmConfig/FetchVist")
    fun visitReportResponseTeam(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("from_date") from_date: String,
                            @Field("to_date") to_date: String): Observable<BeatTeamResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): VisitReportApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(VisitReportApi::class.java)
        }
    }
}