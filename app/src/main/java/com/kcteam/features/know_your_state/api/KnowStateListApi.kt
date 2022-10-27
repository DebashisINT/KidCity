package com.kcteam.features.know_your_state.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.know_your_state.model.KnowYourStateListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 27-11-2019.
 */
interface KnowStateListApi {

    @FormUrlEncoded
    @POST("KnowYourState/KYSDetails")
    fun getKnowStateList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("month") month: String,
                     @Field("year") year: String): Observable<KnowYourStateListResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): KnowStateListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(KnowStateListApi::class.java)
        }
    }

}