package com.kcteam.features.dailyPlan.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.dailyPlan.model.AllPlanListResponseModel
import com.kcteam.features.dailyPlan.model.GetPlanDetailsResponseModel
import com.kcteam.features.dailyPlan.model.GetPlanListResponseModel
import com.kcteam.features.dailyPlan.model.UpdatePlanListInputParamsModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 24-12-2019.
 */
interface PlanApi {

    @FormUrlEncoded
    @POST("FundPlan/FundPlanList")
    fun getPlanList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<GetPlanListResponseModel>

    @POST("FundPlan/UpdateFundPlan")
    fun updatePlanList(@Body updatePlan: UpdatePlanListInputParamsModel): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("FundPlan/FundPlanDetailsList")
    fun getPlanListDetails(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                           @Field("plan_id") plan_id: String): Observable<GetPlanDetailsResponseModel>

    @FormUrlEncoded
    @POST("FundPlan/GetAllFundPlanList")
    fun getAllPlanList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<AllPlanListResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): PlanApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(PlanApi::class.java)
        }
    }

}