package com.kcteam.features.member.model


import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 03-03-2020.
 */
data class TeamListResponseModel(var team_struct: String? = null,
                                 var member_list: ArrayList<TeamListDataModel>? = null) : BaseResponse(), Serializable


data class TeamListDataModel(var user_id: String = "",
                             var user_name: String = "",
                             var contact_no: String = "",
                             var super_id: String = "",
                             var super_name: String = "",
                             var isLeavePending: Boolean = false,
                             var isLeaveApplied: Boolean = false) : Serializable


data class TeamShopListResponseModel(var team_struct: String? = null,
                                     var shop_list: ArrayList<TeamShopListDataModel>? = null) : BaseResponse(), Serializable

data class TeamShopListDataModel(var shop_id: String = "",
                                 var shop_name: String = "",
                                 var shop_lat: String = "",
                                 var shop_long: String = "",
                                 var shop_address: String = "",
                                 var shop_pincode: String = "",
                                 var shop_contact: String = "",
                                 var total_visited: String = "",
                                 var last_visit_date: String = "",
                                 var shop_type: String = "",
                                 var dd_name: String = "",
                                 var entity_code: String = "",
                                 var area_id: String = "",
                                 var model_id: String = "",
                                 var primary_app_id: String = "",
                                 var secondary_app_id: String = "",
                                 var lead_id: String = "",
                                 var funnel_stage_id: String = "",
                                 var stage_id: String = "",
                                 var booking_amount: String = "",
                                 var type_id: String = "",
                                 var user_id: String = "",
                                 var assign_to_pp_id: String = "",
                                 var assign_to_dd_id: String = "",
                                 var owner_name:String = "",
                                 var total_visit_count:String="") : Serializable

data class TeamLocListResponseModel(var total_distance: String = "",
                                    var total_visit_distance: String = "",
                                    var location_details: ArrayList<TeamLocDataModel>? = null) : BaseResponse(), Serializable

data class TeamLocDataModel(var id: String = "",
                            var location_name: String = "",
                            var latitude: String = "",
                            var longitude: String = "",
                            var distance_covered: String = "",
                            var last_update_time: String = "",
                            var shops_covered: String = "",
                            var meetings_attended: String = "",
                            var network_status: String = "",
                            var battery_percentage: String = "") : Serializable

data class TeamPjpResponseModel(val supervisor_name: String = "",
                                val pjp_list: ArrayList<TeamPjpDataModel>? = null) : BaseResponse(), Serializable

data class TeamPjpDataModel(var id: String = "",
                            var from_time: String = "",
                            var to_time: String = "",
                            var customer_name: String = "",
                            var customer_id: String = "",
                            var location: String = "",
                            var date: String = "",
                            var remarks: String = "",
                            var user_id: String = "",
                            var isUpdateable: Boolean = false,
                            var pjp_lat: String = "",
                            var pjp_long: String = "",
                            var pjp_radius: String = "") : Serializable


data class TeamPjpConfigResponseModel(var pjp_past_days: String = "",
                                      var supervisor_name: String = "") : BaseResponse(), Serializable

data class CustomerResponseModel(var cust_list: ArrayList<CustomerDataModel>? = null) : BaseResponse(), Serializable

data class CustomerDataModel(var cust_id: String = "",
                             var cust_name: String = "") : Serializable

data class AddpjpInputParams(var session_token: String = "",
                             var user_id: String = "",
                             var creater_user_id: String = "",
                             var date: String = "",
                             var from_time: String = "",
                             var to_time: String = "",
                             var cust_id: String = "",
                             var location: String = "",
                             var remarks: String = "",
                             var pjp_lat: String = "",
                             var pjp_long: String = "",
                             var pjp_radius: String = "") : Serializable

data class EditPjpInputParams(var session_token: String = "",
                              var user_id: String = "",
                              var creater_user_id: String = "",
                              var date: String = "",
                              var from_time: String = "",
                              var to_time: String = "",
                              var cust_id: String = "",
                              var location: String = "",
                              var remarks: String = "",
                              var PJP_id: String = "",
                              var pjp_lat: String = "",
                              var pjp_long: String = "",
                              var pjp_radius: String = "") : Serializable

data class UserPjpResponseModel(val pjp_list: ArrayList<UserPjpDataModel>? = null) : BaseResponse(), Serializable

data class UserPjpDataModel(var id: String = "",
                            var from_time: String = "",
                            var to_time: String = "",
                            var customer_name: String = "",
                            var customer_id: String = "",
                            var location: String = "",
                            var date: String = "",
                            var remarks: String = "") : Serializable

data class PJPMapData(var lat: Double = 0.0,
                      var long: Double = 0.0,
                      var radius: String = "",
                      var address: String = "") : Serializable

data class TeamAreaListResponseModel(var area_list: ArrayList<TeamAreaListDataModel>? = null) : BaseResponse(), Serializable

data class TeamAreaListDataModel(var area_id: String = "",
                                 var area_name: String = "",
                                 var user_id: String = "") : Serializable