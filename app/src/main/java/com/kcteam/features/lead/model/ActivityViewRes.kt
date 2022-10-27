package com.kcteam.features.lead.model


data class ActivityViewRes(
        var status:String,
        var message:String,
        var crm_id:String,
                       var activity_dtls_list:ArrayList<activity_dtls_list>)

data class activity_dtls_list(var activity_id:String,
                              var activity_date:String,
                              var activity_time:String,
                              var activity_status:String,
                              var activity_type_name:String,
                              var activity_details:String,
                              var other_remarks:String,
                              var activity_next_date:String
                              )