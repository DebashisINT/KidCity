package com.kcteam.features.chat.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.billing.api.AddBillingApi
import com.kcteam.features.billing.model.AddBillingInputParamsModel
import com.kcteam.features.chat.model.ChatListResponseModel
import com.kcteam.features.chat.model.ChatUserResponseModel
import com.kcteam.features.chat.model.GroupUserResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ChatApi {

    @FormUrlEncoded
    @POST("Chat/UserList")
    fun chatUserList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<ChatUserResponseModel>

    @FormUrlEncoded
    @POST("Chat/SendMsg")
    fun sendChat(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                 @Field("msg_id") msg_id: String, @Field("msg") msg: String, @Field("to_id") to_id: String,
                 @Field("time") time: String, @Field("user_name") user_name: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Chat/ChatList")
    fun chatList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                 @Field("opponent_id") opponent_id: String, @Field("page_no") page_no: String,
                 @Field("page_count") page_count: String): Observable<ChatListResponseModel>

    @FormUrlEncoded
    @POST("Chat/GroupUserList")
    fun groupUserList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<GroupUserResponseModel>

    @FormUrlEncoded
    @POST("Chat/AddGroup")
    fun addGrp(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
               @Field("grp_name") grp_name: String, @Field("ids") ids: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Chat/AddmemberToGroup")
    fun addMember(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                  @Field("grp_id") grp_id: String, @Field("ids") ids: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Chat/GroupSelectedUserList")
    fun memberUserList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("grp_id") grp_id: String): Observable<GroupUserResponseModel>

    @FormUrlEncoded
    @POST("Chat/GroupSelectedUserList")
    fun grpMemberList(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                       @Field("grp_id") grp_id: String): Observable<GroupUserResponseModel>

    @FormUrlEncoded
    @POST("Chat/UpdateStatus")
    fun updateChatStatus(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                         @Field("to_id") to_id: String): Observable<BaseResponse>


    /**
     * Companion object to create the ShopDurationApi
     */
    companion object Factory {
        fun create(): ChatApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(ChatApi::class.java)
        }
    }
}