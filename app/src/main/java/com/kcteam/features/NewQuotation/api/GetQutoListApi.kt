package com.kcteam.features.NewQuotation.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.NewQuotation.model.*
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import com.kcteam.features.timesheet.model.TimeSheetListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GetQutoListApi {


    //@POST("SalesQuotation/SalesQuotationSave")
    @POST("SalesQuotation/SalesDocumentNoQuotationSave")
    fun getAddQuot(@Body addShop: AddQuotRequestData?): Observable<BaseResponse>



    @FormUrlEncoded
    @POST("SalesQuotation/ShopWiseSalesQuotationList")
    fun getQuotList(@Field("shop_id") shop_id: String):Observable<ViewQuotResponse>

    @FormUrlEncoded
    @POST("SalesQuotation/SalesQuotationList")
    fun getQuotDetailsList(@Field("quotation_number") quotation_number: String):Observable<ViewDetailsQuotResponse>

    @FormUrlEncoded
    @POST("SalesQuotation/SalesDocumentNoQuotationList")
    fun getDocDetailsList(@Field("document_number") quotation_number: String):Observable<ViewDetailsQuotResponse>

    @FormUrlEncoded
    @POST("SalesQuotation/SalesQuotationDelete")
    fun QuotDel(@Field("quotation_number") quotation_number: String):Observable<BaseResponse>

    @POST("SalesQuotation/SalesQuotationEdit")
    fun editQuot(@Body addShop: EditQuotRequestData?): Observable<BaseResponse>




    companion object Factory {
        fun create(): GetQutoListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(GetQutoListApi::class.java)
        }


        fun createFacePic(): GetQutoListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(GetQutoListApi::class.java)
        }


        fun createMultiPart(): GetQutoListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(GetQutoListApi::class.java)
        }

    }


}