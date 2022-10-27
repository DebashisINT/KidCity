package com.kcteam.features.survey

data class QaListResponseModel(
    var status: String? = null,
    var message: String? = null,
    var Question_list: ArrayList<Question_list>? = null
)


data class Question_list(
var question_id: String? = null,
var question_desc: String? = null,
var question_type:String? = null,
var question_value: String? = null,
var question_for_shoptype_id: String? = null,
var group_name:String? = null)

data class CheckB(var isChk:Boolean,var value:String)

data class SaveQAModel(var user_id:String="",var shop_id:String="",var survey_id:String="",var date_time:String="",var group_name:String="",
            var question_for_shoptype_id:String="",var answer_list:ArrayList<SaveQAListModel>? = null)

data class SaveQAListModel(var question_id:String="",var answer:String="")

data class SurveyQAIMGModel(var user_id:String="",var session_token:String="",var question_id:String="",var survey_id:String="",
                            var img_link:String="")