package com.kcteam.features.micro_learning.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

data class MicroLearningResponseModel(var micro_learning_list: ArrayList<MicroLearningDataModel>? = null) : BaseResponse(), Serializable

data class MicroLearningDataModel(var id: String = "",
                                  var description: String  = "",
                                  var category_name: String = "",
                                  var file_name: String = "",
                                  var file_size: String = "",
                                  var thumbnail: String = "",
                                  var note: String = "",
                                  var isVideo: Boolean = false,
                                  var current_window: String = "",
                                  var play_back_position: String = "",
                                  var play_when_ready: Boolean = true,
                                  var url: String = "",
                                  var isDownloaded: Boolean = false,
                                  var video_duration: String = "") : Serializable