package com.kcteam.features.task.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 12-Aug-20.
 */
data class AddTaskInputModel(var session_token: String = "",
                             var user_id: String = "",
                             var id: String = "",
                             var date: String = "",
                             var task: String = "",
                             var details: String = "",
                             var isCompleted: Boolean = false,
                             var eventID: String = ""): Serializable


data class TaskListResponseModel(var task_list: ArrayList<TaskListDataModel>?=null): BaseResponse(), Serializable

data class TaskListDataModel(var id: String = "",
                             var date: String = "",
                             var task: String = "",
                             var details: String = "",
                             var isCompleted: Boolean = false,
                             var eventID: String = ""): Serializable