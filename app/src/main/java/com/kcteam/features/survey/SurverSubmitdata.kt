package com.kcteam.features.survey


data class surverSubmitdata( var session_token: String? = null,
                             var user_id: String? = null,
                             var shop_id:String? = null,
                             var survey_id:String? = null,
                             var date_time:String? = null,
                             var group_name:String? = null,
                             var question_for_shoptype_id:String? = null,
                             var answer_list: ArrayList<answer_list>? = null)


data class answer_list(
    var question_id: String? = null,
    var answer: String? = null)