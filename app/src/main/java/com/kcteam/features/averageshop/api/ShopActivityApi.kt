package com.kcteam.features.averageshop.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.averageshop.model.ShopActivityRequest
import com.kcteam.features.averageshop.model.ShopActivityResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Pratishruti on 07-12-2017.
 */
interface ShopActivityApi {
    @POST("Daywiseshop/Records")
    fun fetchShopActivity(@Body shopActivityRequest: ShopActivityRequest?): Observable<ShopActivityResponse>

    @FormUrlEncoded
    @POST("Daywiseshop/Records")
    fun fetchShopActivitynew(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("date_span") date_span: String,
                             @Field("from_date") from_date: String,@Field("to_date") to_date: String): Observable<ShopActivityResponse>

    /**
     * Companion object to create the ShopActivityApi
     */
    companion object Factory {
        fun create(): ShopActivityApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ShopActivityApi::class.java)
        }
    }
}