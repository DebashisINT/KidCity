package com.kcteam.features.damageProduct.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.damageProduct.model.DamageProductResponseModel
import com.kcteam.features.damageProduct.model.delBreakageReq
import com.kcteam.features.damageProduct.model.viewAllBreakageReq
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface GetDamageProductListApi {

    @POST("BreakageMaterialsInfo/ListForBreakageMaterials")
    fun viewBreakage(@Body viewBreakage: viewAllBreakageReq?): Observable<DamageProductResponseModel>

    @FormUrlEncoded
    @POST("BreakageMaterialsDetectionInfo/BreakageMaterialDelete")
    fun BreakageDel(@Field("user_id") user_id: String, @Field("breakage_number") breakage_number: String,@Field("session_token") session_token: String):
            Observable<BaseResponse>

    companion object Factory {
        fun create(): GetDamageProductListApi {
            val retrofit = Retrofit.Builder()
                .client(NetworkConstant.setTimeOutNoRetry())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NetworkConstant.BASE_URL)
                .build()

            return retrofit.create(GetDamageProductListApi::class.java)
        }


        fun createFacePic(): GetDamageProductListApi {
            val retrofit = Retrofit.Builder()
                .client(NetworkConstant.setTimeOut())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                .build()

            return retrofit.create(GetDamageProductListApi::class.java)
        }


        fun createMultiPart(): GetDamageProductListApi {
            val retrofit = Retrofit.Builder()
                .client(NetworkConstant.setTimeOut())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(NetworkConstant.ADD_SHOP_BASE_URL)
                .build()

            return retrofit.create(GetDamageProductListApi::class.java)
        }

    }
}