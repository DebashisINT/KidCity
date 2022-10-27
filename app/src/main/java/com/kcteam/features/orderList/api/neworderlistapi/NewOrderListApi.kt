package com.kcteam.features.orderList.api.neworderlistapi

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.login.model.ShopFeedbackResponseModel
import com.kcteam.features.orderList.model.NewOrderListResponseModel
import com.kcteam.features.orderList.model.ReturnListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 03-12-2018.
 */
interface NewOrderListApi {
    @FormUrlEncoded
    @POST("Order/OrderDetailsShopList")
    fun getOrderList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("date") date: String):
            Observable<NewOrderListResponseModel>


    @FormUrlEncoded
    @POST("RubyFoodLead/OrderReturnDetailsList")
    fun getReturnList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("date") date: String):
            Observable<ReturnListResponseModel>

    @FormUrlEncoded
    @POST("Shoplist/ShopActivityFeedbackList")
    fun getShopFeedbackApi(@Field("user_id") user_id: String,@Field("from_date") from_date: String
    ,@Field("to_date") to_date: String,@Field("date_span") date_span: String):
            Observable<ShopFeedbackResponseModel>

    @FormUrlEncoded
    @POST("Order/OrderShopMail")
    fun sendOrderEmail(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("order_id") order_id: String,
                       @Field("shop_id") shop_id: String, @Field("type") type: String):
            Observable<BaseResponse>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): NewOrderListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(NewOrderListApi::class.java)
        }
    }
}