package com.kcteam.features.newcollectionreport

data class PendingCollDtlsData(var shop_id:String,var shop_name:String,
                               var order_id: String,var order_date:String,var order_amt:String,
                               var invoice_id: String,var invoice_date:String,var invoice_amt:String,var coll_list:ArrayList<CollectionList>,var pendingAmt:String,var bill_id:String )

data class CollectionList(var coll_id:String,var coll_amt:String,var coll_date:String)
