package com.kcteam.features.shopdetail.presentation.api

/**
 * Created by Saikat on 10-10-2018.
 */
object EditShopRepoProvider {
    fun provideEditShopRepository(): EditShopRepo {
        return EditShopRepo(EditShopApi.create())
    }

    fun provideEditShopWithoutImageRepository(): EditShopRepo {
        return EditShopRepo(EditShopApi.createWithoutMultipart())
    }
}