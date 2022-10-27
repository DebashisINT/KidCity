package com.kcteam.features.lead.model


data class CustomerLeadResponse(var status:String,var message:String ,var enquiry_from:String,var user_id:String,
 var customer_dtls_list:ArrayList<CustomerLeadList>)

data class CustomerLeadList (var crm_id:String,
                             var customer_name:String,
                             var mobile_no:String,
                             var email:String,
                             var customer_addr:String,
                             var qty:String,
                             var UOM:String,
                             var order_value:String,
                             var enquiry_details:String,
                             var product_req:String,
                             var contact_person:String,
                             var date:String,
                             var time:String,
                             var source_vend_type:String,
                             var status:String)
