package com.kcteam.features.reimbursement.api.reimbursement_list_api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.reimbursement.model.reimbursementlist.ReimbursementListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 25-01-2019.
 */
interface ReimbursementListApi {
    @FormUrlEncoded
    @POST("BillsUpload/ConveyanceList")
    fun getReimbursementList(@Field("user_id") user_id: String, @Field("session_token") session_token: String,
                               @Field("month") month: String, @Field("year") year: String, @Field("visit_type") visit_type: String): Observable<ReimbursementListResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ReimbursementListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ReimbursementListApi::class.java)
        }
    }
}