package com.kcteam.features.survey


class viewsurveyModel(var status: String? = null,
                      var message:String? = null,
                      var user_id: String? = null,
                      var shop_id:String? = null,
                      var survey_list: ArrayList<survey_list>? = null)

data class survey_list(
    var survey_id:String? = null,
    var saved_date_time:String? = null,
    var question_for_shoptype_id:String? = null,
    var group_name:String? = null,
    var answer: String? = null,
    var question_ans_list: ArrayList<question_ans_list>? = null)

data class question_ans_list(
    var question_id:String? = null,
    var question_desc:String? = null,
    var answer:String? = null,
    var image_link:String? = null)

