package com.kcteam.features.NewQuotation.model




data class ViewQuotResponse(var status:String?=null,
                            var message:String?=null,
                            var shop_id:String?=null,
                            var shop_name:String?=null,
                            var shop_phone_no:String?=null,
                            var shop_wise_quotation_list: ArrayList<shop_wise_quotation_list>? = null

)
data class shop_wise_quotation_list(var quotation_number :String?=null,
                                    var save_date_time :String?=null,
                                    var quotation_status :String?=null,
                                    var document_number :String?=null
)