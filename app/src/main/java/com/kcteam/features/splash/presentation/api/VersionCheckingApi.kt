package com.kcteam.features.splash.presentation.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.splash.presentation.model.VersionCheckingReponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 02-01-2019.
 */
interface VersionCheckingApi {
    @FormUrlEncoded
    @POST("Version/Checking")
    fun versionChecking(@Field("devicetype") devicetype: String): Observable<VersionCheckingReponseModel>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): VersionCheckingApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(VersionCheckingApi::class.java)
        }
    }
}