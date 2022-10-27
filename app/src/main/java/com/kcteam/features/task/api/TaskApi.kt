package com.kcteam.features.task.api

import com.kcteam.app.NetworkConstant
import com.kcteam.base.BaseResponse
import com.kcteam.features.task.model.AddTaskInputModel
import com.kcteam.features.task.model.TaskListResponseModel
import com.kcteam.features.timesheet.api.TimeSheetApi
import com.kcteam.features.timesheet.model.TimeSheetListResponseModel
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * Created by Saikat on 12-Aug-20.
 */
interface TaskApi {

    @POST("Task/AddTask")
    fun addTask(@Body addTask: AddTaskInputModel): Observable<BaseResponse>

    @POST("Task/EditTask")
    fun editTask(@Body addTask: AddTaskInputModel): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Task/UpdateTask")
    fun updateStatus(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                     @Field("id") id: String, @Field("isCompleted") isCompleted: Boolean): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Task/DeleteTask")
    fun deleteTask(@Field("session_token") session_token: String, @Field("user_id") user_id: String,
                     @Field("id") id: String): Observable<BaseResponse>

    @FormUrlEncoded
    @POST("Task/TaskList")
    fun taskList(@Field("session_token") session_token: String, @Field("user_id") user_id: String): Observable<TaskListResponseModel>

    companion object Factory {
        fun create(): TaskApi {
            val retrofit = Retrofit.Builder()
                    .client(NetworkConstant.setTimeOutNoRetry())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(NetworkConstant.BASE_URL)
                    .build()

            return retrofit.create(TaskApi::class.java)
        }
    }

}