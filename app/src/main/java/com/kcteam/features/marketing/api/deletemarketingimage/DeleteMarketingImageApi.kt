package com.kcteam.features.marketing.api.deletemarketingimage

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Pratishruti on 28-02-2018.
 */
interface DeleteMarketingImageApi {

    @FormUrlEncoded
    @POST("DeletematerialImage/Delete")
    fun deleteMarketingImage(@Field("user_id") user_id: String, @Field("shop_id") shop_id: String,@Field("image_id") image_id: String): Observable<BaseResponse>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): DeleteMarketingImageApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(DeleteMarketingImageApi::class.java)
        }
    }
}
