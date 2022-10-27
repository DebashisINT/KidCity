package com.kcteam.features.login.api.user_config

import com.kcteam.app.NetworkConstant
import com.kcteam.features.login.model.userconfig.UserConfigResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 14-01-2019.
 */
interface UserConfigApi {
    @FormUrlEncoded
    @POST("Configuration/Userwise")
    fun getUserConfigResponse(@Field("user_id") user_id: String): Observable<UserConfigResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): UserConfigApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(UserConfigApi::class.java)
        }
    }
}