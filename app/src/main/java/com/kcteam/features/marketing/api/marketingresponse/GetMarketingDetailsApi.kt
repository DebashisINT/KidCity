package com.kcteam.features.marketing.api.marketingresponse

import com.kcteam.app.NetworkConstant
import com.kcteam.features.marketing.model.GetMarketingDetailsResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Pratishruti on 05-03-2018.
 */
interface GetMarketingDetailsApi {

    @FormUrlEncoded
    @POST("Marketingdetailslist/Details")
    fun getMarketingDetails(@Field("shop_id") shop_id: String, @Field("user_id") user_id: String): Observable<GetMarketingDetailsResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): GetMarketingDetailsApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(GetMarketingDetailsApi::class.java)
        }
    }
}