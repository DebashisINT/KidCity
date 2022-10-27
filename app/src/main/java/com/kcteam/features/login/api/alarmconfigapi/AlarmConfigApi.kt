package com.kcteam.features.login.api.alarmconfigapi

import com.kcteam.app.NetworkConstant
import com.kcteam.features.login.model.alarmconfigmodel.AlarmConfigResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 19-02-2019.
 */
interface AlarmConfigApi {

    @FormUrlEncoded
    @POST("AlarmConfig/Configuration")
    fun alarmConfigResponse(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<AlarmConfigResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): AlarmConfigApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AlarmConfigApi::class.java)
        }
    }


}