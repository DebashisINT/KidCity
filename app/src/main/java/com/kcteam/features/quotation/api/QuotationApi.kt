package com.kcteam.features.quotation.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.quotation.model.AddQuotInputModel
import com.kcteam.features.quotation.model.BSListResponseModel
import com.kcteam.features.quotation.model.QuotationListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 12-Jun-20.
 */
interface QuotationApi {

    @FormUrlEncoded
    @POST("BS/List")
    fun getBSList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<BSListResponseModel>

    @POST("Quotation/AddQuotation")
    fun AddQuotation(@Body addInput: AddQuotInputModel): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Quotation/List")
    fun getQuotationList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<QuotationListResponseModel>

    @FormUrlEncoded
    @POST("AchivemetReport/QuotationSMSMail")
    fun sendQuotationMailSms(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                             @Field("quo_id") quo_id: String, @Field("shop_id") shop_id: String, @Field("isSms") isSms: Boolean):
            Observable<BaseResponse>


    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): QuotationApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(QuotationApi::class.java)
        }
    }
}