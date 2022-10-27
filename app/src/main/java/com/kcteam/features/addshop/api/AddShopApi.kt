package com.kcteam.features.addshop.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.model.AddQuestionSubmitRequestData
import com.kcteam.features.addshop.model.AddShopRequestData
import com.kcteam.features.addshop.model.AddShopResponse
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/**
 * Created by Pratishruti on 22-11-2017.
 */
interface AddShopApi {


    @POST("RubyFoodLead/QuestionListSave")
    fun getAddQuestionSubmit(@Body addQuestion:AddQuestionSubmitRequestData?): Observable<BaseResponse>

    @POST("RubyFoodLead/QuestionListEdit")
    fun getAddQuestionUpdateSubmit(@Body addQuestion:AddQuestionSubmitRequestData?): Observable<BaseResponse>

    //02-11-2021
    @FormUrlEncoded
    @POST("DuplicateRecords/PhoneNo")
    fun getDuplicationshopPhoneNumber(@Field("user_id") user_id: String,@Field("session_token") session_token: String,@Field("new_shop_phone") new_shop_phone: String):
            Observable<BaseResponse>

    @POST("Shoplist/AddShop")
    fun getAddShop(@Body addShop: AddShopRequestData?): Observable<AddShopResponse>

    @Multipart
    @POST("ShopRegistration/NewShopRegister")
    fun getAddShopWithDocImage(@Query("data") addShop: String, @Part logo_img_data: MultipartBody.Part?): Observable<AddShopResponse>

    @Multipart
    @POST("ShopRegistration/AddCompetitorImage")
    fun getAddShopCompetetorImage(@Query("data") addShop: String, @Part competitor_img: MultipartBody.Part?): Observable<BaseResponse>

    /*9-12-2021*/
    @Multipart
    @POST("RubyLeadImage/RubyLeadImage1Save")
    fun getAddShopUploadImage(@Query("data") addImageupload: String, @Part competitor_img: MultipartBody.Part?): Observable<BaseResponse>
    @Multipart
    @POST("RubyLeadImage/RubyLeadImage2Save")
    fun getAddShopUploadImage2(@Query("data") addImageupload: String, @Part competitor_img: MultipartBody.Part?): Observable<BaseResponse>
    /*9-12-2021*/

    @Multipart
    @POST("ShopRegistration/RegisterShop")
    fun getAddShopWithImage(@Query("data") addShop: String, @Part logo_img_data: MultipartBody.Part?): Observable<AddShopResponse>

    @Multipart
    @POST("ShopRegistration/RegisterShop")
    fun getAddShopWithoutImage(@Query("data") addShop: String): Observable<AddShopResponse>


    @Multipart
    @POST("MultipartFile/upload")
    fun uploadImage(@Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): AddShopApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(AddShopApi::class.java)
        }

        fun createWithoutMultipart(): AddShopApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AddShopApi::class.java)
        }
    }
}