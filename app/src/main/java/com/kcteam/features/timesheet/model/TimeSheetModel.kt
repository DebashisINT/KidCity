package com.kcteam.features.timesheet.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 29-Apr-20.
 */
data class TimeSheetListResponseModel(var superviser_name: String = "",
                                      var total_hrs: String = "",
                                      var timesheet_list: ArrayList<TimeSheetListDataModel>? = null) : Serializable, BaseResponse()

data class TimeSheetListDataModel(var id: String = "",
                                  var time: String = "",
                                  var client_id: String = "",
                                  var client_name: String = "",
                                  var product_id: String = "",
                                  var product_name: String = "",
                                  var project_id: String = "",
                                  var project_name: String = "",
                                  var date: String = "",
                                  var activity_id: String = "",
                                  var activity_name: String = "",
                                  var comments: String = "",
                                  var isUpdateable: Boolean? = null,
                                  var timesheet_status: String = "",
                                  var image: String = "") : Serializable

data class TimeSheetConfigResponseModel(var timesheet_past_days: String = "",
                                        var supervisor_name: String = "",
                                        var client_text: String = "",
                                        var project_text: String = "",
                                        var product_text: String = "",
                                        var activity_text: String = "",
                                        var time_text: String = "",
                                        var comment_text: String = "",
                                        var submit_text: String = "") : Serializable, BaseResponse()

data class TimeSheetDropDownResponseModel(var client_list: ArrayList<TimeSheetClientDataModel>? = null,
                                          var project_list: ArrayList<TimeSheetProjectDataModel>? = null,
                                          var activity_list: ArrayList<TimeSheetActivityDataModel>? = null,
                                          var product_list: ArrayList<TimeSheetProductDataModel>? = null) : Serializable, BaseResponse()

data class TimeSheetClientDataModel(var client_id: String = "",
                                    var client_name: String = "") : Serializable

data class TimeSheetProjectDataModel(var project_id: String = "",
                                     var project_name: String = "") : Serializable

data class TimeSheetActivityDataModel(var activity_id: String = "",
                                      var activity_name: String = "") : Serializable

data class TimeSheetProductDataModel(var product_id: String = "",
                                     var product_name: String = "") : Serializable

data class AddTimeSheetInputModel(var session_token: String = "",
                                  var user_id: String = "",
                                  var date: String = "",
                                  var client_id: String = "",
                                  var project_id: String = "",
                                  var activity_id: String = "",
                                  var product_id: String = "",
                                  var time: String = "",
                                  var comments: String = "",
                                  var timesheet_id: String = "") : Serializable

data class EditTimeSheetInputModel(var session_token: String = "",
                                   var user_id: String = "",
                                   var date: String = "",
                                   var client_id: String = "",
                                   var project_id: String = "",
                                   var activity_id: String = "",
                                   var product_id: String = "",
                                   var time: String = "",
                                   var comments: String = "",
                                   var timesheet_id: String = "") : Serializable

data class EditDeleteTimesheetResposneModel(var timesheet_status: String = ""): BaseResponse(), Serializable