package com.kcteam.features.beatCustom

import com.kcteam.features.lead.model.CustomerLeadList


data class BeatGetStatusModel(var status:String?=null,
                                      var message:String?=null,
                                      var beat_id:String?=null,
                                      var beat_name:String?=null)

data class BeatUpdateModel(var status:String?=null,
                           var message:String?=null,
                           var updated_beat_id:String?=null,
                           var beat_name:String?=null)


data class BeatTeamResponseModel(var status:String?=null,
                    var message:String?=null,var visit_report_list:ArrayList<VisitreportList>)

data class VisitreportList (var user_id:String,var member_name:String,
                            var visit_details_list:ArrayList<VisitdetailsList>)

data class VisitdetailsList (var beat_id:String,
                             var beat_name:String,
                             var visit_status:String,
                             var date:String,var shop_name:String,var visit_time:String)


data class BeatViewModel(var date:String="",var beatName:String="",var beatList:ArrayList<BeatViewListModel> = ArrayList())
data class BeatViewListModel(var cusName:String,var status:String,var vTime:String)
