package com.kcteam.features.nearbyuserlist.api

import com.kcteam.app.Pref
import com.kcteam.features.nearbyuserlist.model.NearbyUserResponseModel
import com.kcteam.features.newcollection.model.NewCollectionListResponseModel
import com.kcteam.features.newcollection.newcollectionlistapi.NewCollectionListApi
import io.reactivex.Observable

class NearbyUserRepo(val apiService: NearbyUserApi) {
    fun nearbyUserList(): Observable<NearbyUserResponseModel> {
        return apiService.getNearbyUserList(Pref.session_token!!, Pref.user_id!!)
    }
}