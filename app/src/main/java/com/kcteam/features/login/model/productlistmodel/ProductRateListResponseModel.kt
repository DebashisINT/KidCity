package com.kcteam.features.login.model.productlistmodel

import com.kcteam.base.BaseResponse
import java.io.Serializable

/**
 * Created by Saikat on 15-01-2020.
 */
class ProductRateListResponseModel : BaseResponse(), Serializable {
    var product_rate_list: ArrayList<ProductRateDataModel>? = null
}