package com.kcteam.features.reimbursement.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.reimbursement.model.ReimbursementConfigResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 24-01-2019.
 */
interface ReimbursementConfigApi {
    @FormUrlEncoded
    @POST("Reimbursement/Configurationfetch")
    fun getReimbursementConfig(@Field("user_id") user_id: String, @Field("state_id") state_id: String): Observable<ReimbursementConfigResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ReimbursementConfigApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ReimbursementConfigApi::class.java)
        }
    }
}