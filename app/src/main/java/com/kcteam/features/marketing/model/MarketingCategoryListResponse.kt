package com.kcteam.features.marketing.model

import com.kcteam.base.BaseResponse

/**
 * Created by Pratishruti on 28-02-2018.
 */
class MarketingCategoryListResponse:BaseResponse() {
    lateinit var RetailBranding:List<MarketingDetailData>
    lateinit var POPMaterial:List<MarketingDetailData>

//    class POPMaterial {
//        var material_id:String?=null
//        var material_name:String?=null
//    }
//    class RetailBranding {
//        var material_id:String?=null
//        var material_name:String?=null
//    }

}





