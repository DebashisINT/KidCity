package com.kcteam.features.addAttendence.api

import com.kcteam.app.NetworkConstant
import com.kcteam.features.addAttendence.model.WorkTypeResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 31-08-2018.
 */
interface WorkTypeListApi {

    @FormUrlEncoded
    @POST("Worktypes/Types")
    fun getWorkType(@Field("user_id") user_id: String): Observable<WorkTypeResponseModel>

    /**
     * Companion object to create the GithubApiService
     */
    companion object Factory {
        fun create(): WorkTypeListApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOut())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(WorkTypeListApi::class.java)
        }
    }

}