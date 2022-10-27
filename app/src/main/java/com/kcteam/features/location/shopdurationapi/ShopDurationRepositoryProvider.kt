package com.kcteam.features.location.shopdurationapi

/**
 * Created by Pratishruti on 29-11-2017.
 */
object ShopDurationRepositoryProvider{
    fun provideShopDurationRepository(): ShopDurationRepository {
        return ShopDurationRepository(ShopDurationApi.Factory.create())
    }
}