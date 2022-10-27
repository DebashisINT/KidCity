package com.kcteam.features.photoReg.api

import com.kcteam.features.stockAddCurrentStock.api.ShopAddStockApi
import com.kcteam.features.stockAddCurrentStock.api.ShopAddStockRepository

object GetUserListPhotoRegProvider {

    fun provideUserListPhotoReg(): GetUserListPhotoRegRepository {
        return GetUserListPhotoRegRepository(GetUserListPhotoRegApi.create())
    }

    fun providePhotoReg(): GetUserListPhotoRegRepository {
        return GetUserListPhotoRegRepository(GetUserListPhotoRegApi.createFacePic())
    }

    fun jobMultipartRepoProvider(): GetUserListPhotoRegRepository {
        return GetUserListPhotoRegRepository(GetUserListPhotoRegApi.createMultiPart())
    }

    fun providePhotoSubmitReg(): GetUserListPhotoRegRepository {
        return GetUserListPhotoRegRepository(GetUserListPhotoRegApi.submitPic())
    }

}