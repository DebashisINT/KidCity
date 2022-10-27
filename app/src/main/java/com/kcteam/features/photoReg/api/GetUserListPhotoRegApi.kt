package com.kcteam.features.photoReg.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.addshop.api.AddShopApi
import com.kcteam.features.location.model.AppInfoResponseModel
import com.kcteam.features.photoReg.model.*
import com.kcteam.features.stockAddCurrentStock.api.ShopAddStockApi
import com.kcteam.features.stockAddCurrentStock.model.CurrentStockGetData
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface GetUserListPhotoRegApi {

    @FormUrlEncoded
    @POST("UserWiseAadharInfo/UserAadharList")
    fun getAllAadhaar(@Field("session_token") session_token: String):
            Observable<GetAllAadhaarResponse>

    @FormUrlEncoded
    @POST("FaceRegistration/UserList")
    fun getUserListApi(@Field("user_id") user_id: String,@Field("session_token") session_token: String):
            Observable<GetUserListResponse>

    @Multipart
    @POST("FaceImageDetection/FaceImage")
    fun getAddUserFaceImage(@Query("data") face: String, @Part attachments: MultipartBody.Part?): Observable<FaceRegResponse>


    @Multipart
    @POST("BreakageMaterialsDetectionInfo/BreakageMaterialsSave")
    fun subAddImage(@Query("data") face: String, @Part attachments: MultipartBody.Part?): Observable<ImageResponse>

    @FormUrlEncoded
    @POST("FaceRegistration/FaceMatch")
    fun getUserFacePic(@Field("user_id") user_id: String,@Field("session_token") session_token: String ): Observable<UserFacePicUrlResponse>


    @FormUrlEncoded
    @POST("FaceImageDetection/FaceImgDelete")
    fun deleteUserPicApi(@Field("user_id") user_id: String,@Field("session_token") session_token: String):
            Observable<DeleteUserPicResponse>

    @Multipart
    @POST("UserAadharImageSave/UserAadharImage")
    fun submitAadhaarDetails(@Query("data") data: String, @Part attachments: List<MultipartBody.Part?>): Observable<BaseResponse>

    @POST("UserWiseAadharInfo/UserAadharInfo")
    fun submitAadhaarDetailsSingle(@Body aadhaarSubmitData: AadhaarSubmitData): Observable<BaseResponse>

    companion object Factory {
        fun create(): GetUserListPhotoRegApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(GetUserListPhotoRegApi::class.java)
        }




        fun createFacePic(): GetUserListPhotoRegApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(GetUserListPhotoRegApi::class.java)
        }

        fun submitPic(): GetUserListPhotoRegApi {
            val retrofit = Retrofit.Builder()
                .client(NetworkConstant.setTimeOut())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                .build()

            return retrofit.create(GetUserListPhotoRegApi::class.java)
        }


        fun createMultiPart(): GetUserListPhotoRegApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(GetUserListPhotoRegApi::class.java)
        }
    }


}