package com.kcteam.features.dashboard.presentation.api.dashboardApi

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.login.api.LoginApi
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Saikat on 26-Jun-20.
 */
interface DashboardApi {

    @Multipart
    @POST("AlermImage/AlermSelfie")
    fun alarmSelfie(@Query("data") alarmData: String, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("HomeLoactionVisit/Submit")
    fun submitHomeLocReason(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("reason") reason: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("EarlyExit/Submit")
    fun submitLogoutReason(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("reason") reason: String): Observable<BaseResponse>



    @Multipart
    @POST("DayStartEndImageSave/DayStartEndImage")
    fun dayStartEndWithImage(@Query("data") addAttendence: String, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): DashboardApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(DashboardApi::class.java)
        }

        fun img(): DashboardApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(DashboardApi::class.java)
        }
    }
}