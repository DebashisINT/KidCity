package com.kcteam.features.billing.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.billing.model.AddBillingInputParamsModel
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * Created by Saikat on 20-02-2019.
 */
interface AddBillingApi {

    @POST("Billing/AddBilling")
    fun addBill(@Body addBilling: AddBillingInputParamsModel?): Observable<BaseResponse>

    /*@Multipart
    @POST("ShopRegistration/AddBillingImage")
    fun addBillWithImage(@Query("data") addBill: String, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>*/

    @Multipart
    @POST("ShopRegistration/AddBillingImage")
    fun addBillWithImage(@Part("data") data: RequestBody, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>

    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): AddBillingApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AddBillingApi::class.java)
        }

        fun createImage(): AddBillingApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(AddBillingApi::class.java)
        }
    }



}