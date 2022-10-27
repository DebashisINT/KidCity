package com.kcteam.features.reimbursement.api.configfetchapi

import com.kcteam.app.NetworkConstant
import com.kcteam.features.reimbursement.model.ReimbursementConfigFetchInputModel
import com.kcteam.features.reimbursement.model.ReimbursementConfigFetchResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Saikat on 24-01-2019.
 */
interface ReimbursementConfigFetchApi {
    //@FormUrlEncoded
    @POST("Reimbursement/ConfigurationFetchvalues")
    fun fetchReimbursementConfig(@Body configFetchInputModel: ReimbursementConfigFetchInputModel): Observable<ReimbursementConfigFetchResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): ReimbursementConfigFetchApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ReimbursementConfigFetchApi::class.java)
        }
    }
}