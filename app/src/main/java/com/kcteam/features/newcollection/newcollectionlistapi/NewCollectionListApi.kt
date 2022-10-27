package com.kcteam.features.newcollection.newcollectionlistapi

import com.kcteam.app.NetworkConstant
import com.kcteam.features.newcollection.model.CollectionDetailsResponseModel
import com.kcteam.features.newcollection.model.CollectionShopListResponseModel
import com.kcteam.features.newcollection.model.NewCollectionListResponseModel
import com.kcteam.features.newcollection.model.PaymentModeResponseModel
import com.kcteam.features.shopdetail.presentation.model.collectionlist.CollectionListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 15-02-2019.
 */
interface NewCollectionListApi {
    @FormUrlEncoded
    @POST("Collection/Collectionlist")
    fun newCollectionList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("collection_date") collection_date: String): Observable<NewCollectionListResponseModel>

    @FormUrlEncoded
    @POST("Collection/CollectionListReport")
    fun newCollectionDetails(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<CollectionDetailsResponseModel>

    @FormUrlEncoded
    @POST("Collection/InvoiceListReport")
    fun newCollectionShopList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                             @Field("date") date: String): Observable<CollectionShopListResponseModel>

    @FormUrlEncoded
    @POST("Collection/PaymentModeList")
    fun paymentModeList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<PaymentModeResponseModel>


    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): NewCollectionListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(NewCollectionListApi::class.java)
        }
    }
}