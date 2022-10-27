package com.kcteam.features.averageshop.api

/**
 * Created by Pratishruti on 07-12-2017.
 */
object ShopActivityRepositoryProvider {
    fun provideShopActivityRepository(): ShopActivityRepository {
        return ShopActivityRepository(ShopActivityApi.create())
    }
}