package com.kcteam.features.addshop.model

import com.kcteam.base.BaseResponse




data class imageListResponse(  var user_id: String? = null,
                               var shop_id: String? = null,
                               var image_list: ArrayList<image_list>? = null) : BaseResponse()



data class image_list(
    val attachment_image1: String,
    val attachment_image2: String,
    val attachment_image3: String,
    val attachment_image4: String
)
