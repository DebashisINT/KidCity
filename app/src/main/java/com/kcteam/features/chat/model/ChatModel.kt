package com.kcteam.features.chat.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

data class ChatUserResponseModel(var chat_user_list: ArrayList<ChatUserDataModel>? = null) : BaseResponse(), Serializable

data class ChatUserDataModel(var id: String = "",
                             var name: String = "",
                             var isGroup: Boolean = false,
                             var image: String = "",
                             var last_msg: String = "",
                             var last_msg_time: String = "",
                             var last_msg_user_id: String = "",
                             var last_msg_user_name: String = "") : Serializable

data class ChatListResponseModel(var chat_list: ArrayList<ChatListDataModel>? = null) : BaseResponse(), Serializable

data class ChatListDataModel(var id: String = "",
                             var msg: String = "",
                             var time: String = "",
                             var from_id: String = "",
                             var from_name: String = "",
                             var status: String = "") : Serializable

data class GroupUserResponseModel(var group_user_list: ArrayList<GroupUserDataModel>? = null) : BaseResponse(), Serializable

data class GroupUserDataModel(var id: String = "",
                              var name: String = "",
                              var image: String = "",
                              var isSelected: Boolean = false) : Serializable