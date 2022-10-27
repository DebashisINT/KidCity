package com.kcteam.features.report.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 22-Jul-20.
 */
data class AchievementResponseModel(var achv_report_list: ArrayList<AchievementDataModel>? = null) : BaseResponse(), Serializable

data class AchievementDataModel(var member_name: String = "",
                                var member_id: String = "",
                                var report_to: String = "",
                                var stage_count: String = "",
                                var achv_details_list: ArrayList<AchievementDetailsModel>?= null): Serializable

data class AchievementDetailsModel(var cust_name: String = "",
                                   var visit_time: String = "",
                                   var visit_date: String = "",
                                   var stage: String = ""): Serializable


data class TargetVsAchvResponseModel(var targ_achv_report_list: ArrayList<TargetVsAchvDataModel>? = null) : BaseResponse(), Serializable

data class TargetVsAchvDataModel(var member_name: String = "",
                                 var member_id: String = "",
                                 var report_to: String = "",
                                 var targ_achv_details_list: ArrayList<TargetVsAchvDetailsModel>?= null): Serializable

data class TargetVsAchvDetailsModel(var enquiry: String = "",
                                   var lead: String = "",
                                   var test_drive: String = "",
                                   var booking: String = "",
                                   var retail: String = ""): Serializable