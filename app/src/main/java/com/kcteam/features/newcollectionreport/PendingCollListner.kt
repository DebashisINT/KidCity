package com.kcteam.features.newcollectionreport

import com.kcteam.features.photoReg.model.UserListResponseModel

interface PendingCollListner {
    fun getUserInfoOnLick(obj: PendingCollData)
}