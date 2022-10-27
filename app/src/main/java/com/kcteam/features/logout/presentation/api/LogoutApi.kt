package com.kcteam.features.logout.presentation.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Pratishruti on 23-11-2017.
 */
interface LogoutApi {
    @FormUrlEncoded
    @POST("Logout/UserLogout")
    fun getLogoutResponse(@Field("user_id") email: String, @Field("session_token") password: String, @Field("latitude") latitude: String,
                          @Field("longitude") longitude: String, @Field("logout_time") logout_time: String, @Field("distance") distance: String,
                          @Field("Autologout") Autologout: String, @Field("address") address: String): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): LogoutApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(LogoutApi::class.java)
        }
    }

}