package com.kcteam.features.dashboard.presentation.model

import com.kcteam.base.BaseResponse

/**
 * Created by Saikat on 03-12-2018.
 */
class SelectedRouteListResponseModel : BaseResponse() {
    var worktype: ArrayList<SelectedRouteListWorkTypeModel>? = null
    var route_list: ArrayList<SelectRouteShopListModel>? = null
}