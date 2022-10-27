package com.kcteam.features.viewAllOrder.api

/**
 * Created by Saikat on 01-10-2018.
 */
object OrderDetailsListRepoProvider {
    fun provideOrderDetailsListRepository(): OrderDetailsListRepo {
        return OrderDetailsListRepo(OrderDetailsListApi.create())
    }

}