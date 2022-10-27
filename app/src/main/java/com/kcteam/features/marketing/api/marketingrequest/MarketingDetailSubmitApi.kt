package com.kcteam.features.marketing.api.marketingrequest

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * Created by Pratishruti on 28-02-2018.
 */
interface MarketingDetailSubmitApi {
    @Multipart
    @POST("SendMarketingdetails/Marketing")
    fun sendMarketingDetailReq(@Query("marketing_detail") marketing_detail: String, @Part logo_img_data: List<MultipartBody.Part>?): Observable<BaseResponse>

//    @Multipart
    @POST("SendMarketingdetails/Marketing")
    fun sendMarketingDetailReqWithoutImg(@Query("marketing_detail") marketing_detail: String): Observable<BaseResponse>


    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): MarketingDetailSubmitApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(MarketingDetailSubmitApi::class.java)
        }
    }

}