package com.kcteam.features.addshop.api.assignedToDDList

import com.kcteam.app.NetworkConstant
import com.kcteam.features.addshop.model.assigntoddlist.AssignToDDListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 03-10-2018.
 */
interface AssignToDDListApi {
    @FormUrlEncoded
    @POST("ShopAssignment/GetAssignedToDDList")
    fun getAssignedToDDList(@Field("session_token") session_token: String, @Field("user_id") user_id: String, @Field("state_id") state_id: String):
            Observable<AssignToDDListResponseModel>

    /**
     * Companion object to create the ShopActivityApi
     */
    companion object Factory {
        fun create(): AssignToDDListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(AssignToDDListApi::class.java)
        }
    }
}