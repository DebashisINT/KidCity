package com.kcteam.features.orderList.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.orderList.model.OrderListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 01-10-2018.
 */
interface OrderListApi {
    @FormUrlEncoded
    @POST("Order/OrderList")
    fun getOrderList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("date") date: String): Observable<OrderListResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): OrderListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(OrderListApi::class.java)
        }
    }
}