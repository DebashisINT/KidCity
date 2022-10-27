package com.kcteam.features.addshop.api.typeList


/**
 * Created by Saikat on 22-Jun-20.
 */
object TypeListRepoProvider {
    fun provideTypeListRepository(): TypeListRepo {
        return TypeListRepo(TypeListApi.create())
    }
}