package com.kcteam.features.login.api.global_config

import com.kcteam.app.NetworkConstant
import com.kcteam.features.login.model.globalconfig.ConfigFetchResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

/**
 * Created by Saikat on 14-01-2019.
 */
interface ConfigFetchApi {

    @POST("Configuration/fetch")
    fun getConfigResponse(): Observable<ConfigFetchResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ConfigFetchApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ConfigFetchApi::class.java)
        }
    }
}