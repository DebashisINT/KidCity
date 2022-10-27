package com.kcteam.features.myjobs.model

import com.kcteam.base.BaseResponse
import java.io.Serializable

data class CustomerListResponseModel(var job_list: ArrayList<CustomerDataModel>? = null) : BaseResponse(), Serializable

data class CustomerDataModel(var id: String = "",
                             var name: String = "",
                             var address: String = "",
                             var latitude: String = "",
                             var longitude: String = "",
                             var contact_person: String = "",
                             var contact_no: String = "",
                             var status: String = "",
                             var service_for: String = "",
                             var total_service: String = "",
                             var service_frequency: String = "",
                             var total_service_commited: String = "",
                             var total_service_pending: String = "",
                             var last_service_committed: String = "",
                             var job_code: String = "",
                             var isShowUpdateStatus: Boolean = false,
                             var date: String = "",
                             var statusCode: String = "") : Serializable

data class CustomerStatusResponseModel(var job_status: String = "",
                                       var last_status: String = "") : BaseResponse(), Serializable

data class CustomerWIPStatusModel(var uom_text: String? = null, var service_due_for: String? = null) : BaseResponse(), Serializable

data class WIPSubmit(var session_token: String = "",
                     var user_id: String = "",
                     var job_id: String = "",
                     var start_date: String = "",
                     var start_time: String = "",
                     var service_due: String = "",
                     var service_completed: String = "",
                     var next_date: String = "",
                     var next_time: String = "",
                     var remarks: String = "",
                     var date_time: String = "",
                     var latitude: String = "",
                     var longitude: String = "",
                     var address: String = "") : Serializable

data class WIPImageSubmit(var link: String = "",
                          var type: String = "") : Serializable

data class WorkOnHoldInputParams(var session_token: String = "",
                                 var user_id: String = "",
                                 var job_id: String = "",
                                 var hold_date: String = "",
                                 var hold_time: String = "",
                                 var reason_hold: String = "",
                                 var remarks: String = "",
                                 var date_time: String = "",
                                 var latitude: String = "",
                                 var longitude: String = "",
                                 var address: String = "") : Serializable

data class WorkUnHoldInputParams(var session_token: String = "",
                                 var user_id: String = "",
                                 var job_id: String = "",
                                 var unhold_date: String = "",
                                 var unhold_time: String = "",
                                 var reason_unhold: String = "",
                                 var remarks: String = "",
                                 var date_time: String = "",
                                 var latitude: String = "",
                                 var longitude: String = "",
                                 var address: String = "") : Serializable

data class WorkCompletedInputParams(var session_token: String = "",
                                    var user_id: String = "",
                                    var job_id: String = "",
                                    var finish_date: String = "",
                                    var finish_time: String = "",
                                    var remarks: String = "",
                                    var phone_no: String = "",
                                    var date_time: String = "",
                                    var latitude: String = "",
                                    var longitude: String = "",
                                    var address: String = "") : Serializable

data class WorkCancelledInputParams(var session_token: String = "",
                                    var user_id: String = "",
                                    var job_id: String = "",
                                    var date: String = "",
                                    var time: String = "",
                                    var cancel_reason: String = "",
                                    var remarks: String = "",
                                    var date_time: String = "",
                                    var latitude: String = "",
                                    var longitude: String = "",
                                    var address: String = "",
                                    var cancelled_by: String = "",
                                    var user: String = "") : Serializable

data class UpdateReviewInputParams(var session_token: String = "",
                                   var user_id: String = "",
                                   var job_id: String = "",
                                   var review: String = "",
                                   var rate: String = "",
                                   var date_time: String = "",
                                   var latitude: String = "",
                                   var longitude: String = "",
                                   var address: String = "") : Serializable

data class WorkCompletedSettingsResponseModel(var isAttachmentMandatory: Boolean = false,
                                              var phone_no: String = "") : BaseResponse(), Serializable

data class CustListResponseModel(var customer_list: ArrayList<CustomerDataModel>? = null) : BaseResponse(), Serializable

data class HistoryResponseModel(var history_list: ArrayList<HistoryDataModel>? = null) : BaseResponse(), Serializable

data class HistoryDataModel(var id: String = "",
                            var schedule_date_time: String = "",
                            var job_code: String = "",
                            var service_for: String = "",
                            var area: String = "",
                            var team: String = "",
                            var status: String = "",
                            var start_date_time: String = "",
                            var service_due_for: String = "",
                            var service_completed_for: String = "",
                            var next_date_time: String = "",
                            var wip_remarks: String = "",
                            var wip_attachment: String = "",
                            var wip_photo: String = "",
                            var hold_date_time: String = "",
                            var hold_reason: String = "",
                            var hold_remarks: String = "",
                            var hold_attachment: String = "",
                            var hold_photo: String = "",
                            var complete_date_time: String = "",
                            var complete_remarks: String = "",
                            var complete_attachment: String = "",
                            var complete_photo: String = "",
                            var cancelled_date_time: String = "",
                            var cancel_reason: String = "",
                            var cancel_remarks: String = "",
                            var cancel_attachment: String = "",
                            var cancel_photo: String = "",
                            var review_details: String = "",
                            var review_attachment: String = "",
                            var review_photo: String = "",
                            var ratings: String = "",
                            var uom_text: String = ""): Serializable