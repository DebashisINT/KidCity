package com.kcteam.features.report.api.report_api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.report.model.AchievementResponseModel
import com.kcteam.features.report.model.TargetVsAchvResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 22-Jul-20.
 */
interface ReportApi {

    @FormUrlEncoded
    @POST("AchivemetReport/AchivementReportList")
    fun getAchievementList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                     @Field("from_date") from_date: String, @Field("to_date") to_date: String): Observable<AchievementResponseModel>

    @FormUrlEncoded
    @POST("AchivemetReport/TargetAchivementReportList")
    fun getTargVsAchvList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                           @Field("from_date") from_date: String, @Field("to_date") to_date: String): Observable<TargetVsAchvResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ReportApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ReportApi::class.java)
        }
    }
}