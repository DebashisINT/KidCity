package com.kcteam.features.myprofile.api.statelist

import com.kcteam.app.NetworkConstant
import com.kcteam.features.myprofile.model.statelist.StateListApiResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Pratishruti on 16-02-2018.
 */
interface StateListApi {

    @GET("StateList/States")
    fun getAllState(): Observable<StateListApiResponse>
    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): StateListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(StateListApi::class.java)
        }
    }


}