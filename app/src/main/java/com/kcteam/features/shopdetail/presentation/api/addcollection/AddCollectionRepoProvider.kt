package com.kcteam.features.shopdetail.presentation.api.addcollection

/**
 * Created by Saikat on 13-11-2018.
 */
object AddCollectionRepoProvider {
    fun addCollectionRepository(): AddCollectionRepo {
        return AddCollectionRepo(AddCollectionApi.create())
    }

    fun addCollectionMultipartRepository(): AddCollectionRepo {
        return AddCollectionRepo(AddCollectionApi.multipart())
    }
}