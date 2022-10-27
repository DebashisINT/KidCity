package com.kcteam.features.login.model.productlistmodel

import com.kcteam.app.domain.ModelEntity
import com.kcteam.app.domain.ProductListEntity
import com.kcteam.base.BaseResponse

class ModelListResponse: BaseResponse() {
    var model_list: ArrayList<ModelEntity>? = null
}