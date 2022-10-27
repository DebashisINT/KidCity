package com.kcteam.fcm.api

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
 * Created by Saikat on 27-02-2019.
 */
interface UpdateDeviceTokenApi {
    @FormUrlEncoded
    @POST("Devicetoken/Update")
    fun updateDeviceToken(@Field("user_id") user_id: String, @Field("session_token") session_token: String, @Field("device_token") device_token: String,
                  @Field("device_type") device_type: String): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): UpdateDeviceTokenApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(UpdateDeviceTokenApi::class.java)
        }
    }
}