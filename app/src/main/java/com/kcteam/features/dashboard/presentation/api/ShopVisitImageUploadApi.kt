package com.kcteam.features.dashboard.presentation.api

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
 * Created by Saikat on 28-09-2018.
 */
interface ShopVisitImageUploadApi {

    @Multipart
    @POST("ShopVisitImageUpload/Revisit")
    fun visitShopWithImage(@Query("data") addShop: String, @Part logo_img_data: MultipartBody.Part?): Observable<BaseResponse>

    @Multipart
    @POST("FileUpload/UploadAudioforShop")
    fun visitShopWithAudio(@Query("data") addShop: String, @Part logo_audio_data: MultipartBody.Part?): Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ShopVisitImageUploadApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                    .build()

            return retrofit.create(ShopVisitImageUploadApi::class.java)
        }
    }
}