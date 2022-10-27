package com.kcteam.features.know_your_state.api

/**
 * Created by Saikat on 27-11-2019.
 */
object KnowStateListRepoProvider {
    fun knowStateListRepoProvider(): KnowStateListRepo {
        return KnowStateListRepo(KnowStateListApi.create())
    }
}