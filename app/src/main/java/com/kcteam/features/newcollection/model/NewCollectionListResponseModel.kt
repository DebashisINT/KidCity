package com.kcteam.features.newcollection.model

import com.kcteam.app.domain.CollectionDetailsEntity
import com.kcteam.base.BaseResponse
import com.kcteam.features.shopdetail.presentation.model.collectionlist.CollectionListDataModel

/**
 * Created by Saikat on 15-02-2019.
 */
class NewCollectionListResponseModel : BaseResponse() {
    //var collection_list: ArrayList<CollectionListDataModel>? = null
    var collection_list: ArrayList<CollectionDetailsEntity>? = null
}