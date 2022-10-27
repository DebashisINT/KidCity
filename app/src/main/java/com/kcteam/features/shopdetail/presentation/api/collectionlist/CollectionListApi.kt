package com.kcteam.features.shopdetail.presentation.api.collectionlist

import com.kcteam.app.NetworkConstant
import com.kcteam.features.billing.model.BillingListResponseModel
import com.kcteam.features.shopdetail.presentation.model.collectionlist.CollectionListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 13-11-2018.
 */
interface CollectionListApi {

    @FormUrlEncoded
    @POST("Collection/ListCollection")
    fun collectionList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("shop_id") shop_id: String): Observable<CollectionListResponseModel>


    @FormUrlEncoded
    @POST("Collection/InvoiceList")
    fun billingList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("shop_id") shop_id: String): Observable<BillingListResponseModel>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): CollectionListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(CollectionListApi::class.java)
        }
    }
}