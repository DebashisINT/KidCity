package com.kcteam.features.login.api.productlistapi

import com.kcteam.app.NetworkConstant
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.features.login.model.productlistmodel.ProductListOfflineResponseModel
import com.kcteam.features.login.model.productlistmodel.ProductListOfflineResponseModelNew
import com.kcteam.features.login.model.productlistmodel.ProductListResponseModel
import com.kcteam.features.login.model.productlistmodel.ProductRateListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 20-11-2018.
 */
interface ProductListApi {
    @FormUrlEncoded
    @POST("ProductList/List")
    fun getProductList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("last_update_date") last_update_date: String): Observable<ProductListResponseModel>


    @FormUrlEncoded
    @POST("ProductList/ProductRate")
    fun getProductRateList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("shop_id") shop_id: String): Observable<ProductRateListResponseModel>

    @FormUrlEncoded
    @POST("ProductList/OfflineProductRate")
    fun getOfflineProductRateList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<ProductListOfflineResponseModel>

    @FormUrlEncoded
    @POST("ProductList/OfflineProductRate")
    fun getOfflineProductRateListNew(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<ProductListOfflineResponseModelNew>


    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ProductListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ProductListApi::class.java)
        }
    }
}