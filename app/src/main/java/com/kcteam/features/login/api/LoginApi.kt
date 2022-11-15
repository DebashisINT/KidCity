package com.kcteam.features.login.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.login.model.GetConcurrentUserResponse
import com.kcteam.features.login.model.LoginResponse
import com.kcteam.features.login.model.NewSettingsResponseModel
import com.kcteam.features.login.model.mettingListModel.MeetingListResponseModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Pratishruti on 23-11-2017.
 */
interface LoginApi {
    @FormUrlEncoded
    @POST("UserLogin/Login")
    fun getLoginResponse(@Field("username") email: String, @Field("password") password: String, @Field("latitude") latitude: String,
                         @Field("longitude") longitude: String, @Field("login_time") login_time: String, @Field("imei") imei: String,
                         @Field("version_name") version: String, @Field("address") address: String, @Field("device_token") device_token: String)
            : Observable<LoginResponse>

    @FormUrlEncoded
    @POST("LoginConcurrentusers/FetchConcurrentUser")
    fun getConcurrentUserDtlsApi(@Field("user_id") user_id: String): Observable<GetConcurrentUserResponse>

    @FormUrlEncoded
    @POST("LoginConcurrentusers/InsertConcurrentUser")
    fun insertConcurrentUserDtlsApi(@Field("user_id") user_id: String,@Field("imei") imei: String,
                                    @Field("date_time") date_time: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("LoginConcurrentusers/DeleteConcurrentUser")
    fun deleteConcurrentUserDtlsApi(@Field("user_id") user_id: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Configuration/MeetingType")
    fun getMeetingList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<MeetingListResponseModel>

    @FormUrlEncoded
    @POST("Configuration/LoginSettings")
    fun getNewSettings(@Field("user_name") email: String, @Field("password") password: String): Observable<NewSettingsResponseModel>

    @Multipart
    @POST("kcteamAttendance/AddLoginImage")
    fun loginWithImage(@Query("data") addAttendence: String, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>


    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): LoginApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(LoginApi::class.java)
        }

        fun loginImg(): LoginApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(LoginApi::class.java)
        }
    }

}