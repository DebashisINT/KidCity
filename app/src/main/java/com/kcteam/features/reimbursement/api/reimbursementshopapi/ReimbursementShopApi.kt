package com.kcteam.features.reimbursement.api.reimbursementshopapi

import com.kcteam.app.NetworkConstant
import com.kcteam.features.reimbursement.model.reimbursement_shop.ReimbursementShopResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 31-05-2019.
 */
interface ReimbursementShopApi {
    @FormUrlEncoded
    @POST("Reimbursement/ReimbursementShop")
    fun getReimbursementShop(@Field("user_id") user_id: String, @Field("session_token") session_token: String,
                             @Field("date") date: String, @Field("isEditable") isEditable: Boolean,
                             @Field("Expense_mapId") Expense_mapId: String, @Field("Subexpense_MapId") Subexpense_MapId: String): Observable<ReimbursementShopResponseModel>

    @FormUrlEncoded
    @POST("Reimbursement/LocationCaptureList")
    fun getReimbursementLoc(@Field("user_id") user_id: String, @Field("session_token") session_token: String,
                             @Field("date") date: String): Observable<ReimbursementShopResponseModel>


    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ReimbursementShopApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ReimbursementShopApi::class.java)
        }
    }
}