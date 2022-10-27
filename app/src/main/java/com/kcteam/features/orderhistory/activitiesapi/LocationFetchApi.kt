package com.kcteam.features.orderhistory.activitiesapi

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.orderhistory.model.FetchLocationRequest
import com.kcteam.features.orderhistory.model.FetchLocationResponse
import com.kcteam.features.orderhistory.model.SubmitLocationInputModel
import com.kcteam.features.orderhistory.model.UnknownReponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Pratishruti on 30-11-2017.
 */
interface LocationFetchApi {
    @POST("Fetchlocation/List")
    fun getLocationUpdates(@Body location: FetchLocationRequest?): Observable<FetchLocationResponse>

    @FormUrlEncoded
    @POST("Fetchlocation/NewList")
    fun getLocationUpdates(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                           @Field("date") date: String): Observable<FetchLocationResponse>

    @FormUrlEncoded
    @POST("LocationAddModify/LocationList")
    fun getUnknownLocation(@Field("session_token") session_token: String, @Field("user_id") user_id: String):
            Observable<UnknownReponseModel>


    @POST("LocationAddModify/LocationModify")
    fun submitLocation(@Body location: SubmitLocationInputModel): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): LocationFetchApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(LocationFetchApi::class.java)
        }
    }

}