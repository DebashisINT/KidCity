package com.kcteam.features.nearbyshops.api.updateaddress

/**
 * Created by Pratishruti on 28-11-2017.
 */
object ShopAddressUpdateRepoProvider {
    fun provideShopAddressUpdateRepo(): ShopAddressUpdateRepo {
        return ShopAddressUpdateRepo(ShopAddressUpdateApi.create())
    }
}