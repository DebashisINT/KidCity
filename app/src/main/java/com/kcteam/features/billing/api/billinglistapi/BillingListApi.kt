package com.kcteam.features.billing.api.billinglistapi

import com.kcteam.app.NetworkConstant
import com.kcteam.features.billing.model.BillingListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 20-02-2019.
 */
interface BillingListApi {
    @FormUrlEncoded
    @POST("Billing/List")
    fun getBillList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("order_id") date: String):
            Observable<BillingListResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): BillingListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(BillingListApi::class.java)
        }
    }
}