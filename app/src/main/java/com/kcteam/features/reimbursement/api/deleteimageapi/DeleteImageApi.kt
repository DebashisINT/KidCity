package com.kcteam.features.reimbursement.api.deleteimageapi

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 08-02-2019.
 */
interface DeleteImageApi {

    @FormUrlEncoded
    @POST("BillsUpload/DeleteReimbersmentImage")
    fun deleteReimbursementImage(@Field("user_id") user_id: String, @Field("session_token") session_token: String,
                                 @Field("Id") Id: String): Observable<BaseResponse>


    companion object Factory {
        fun create(): DeleteImageApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(DeleteImageApi::class.java)
        }
    }
}