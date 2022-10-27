package com.kcteam.features.marketing.api.marketingcategorylist

import com.kcteam.app.NetworkConstant
import com.kcteam.features.marketing.model.MarketingCategoryListResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created by Pratishruti on 28-02-2018.
 */
interface MarketingCategoryListApi {

    @GET("Marketinglist/List")
    fun getMarketingCategoryList(): Observable<MarketingCategoryListResponse>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): MarketingCategoryListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(MarketingCategoryListApi::class.java)
        }
    }
}
