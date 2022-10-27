package com.kcteam.features.myprofile.api.statelist

/**
 * Created by Pratishruti on 19-02-2018.
 */
object StateListRepoProvider {
    fun provideStateListRepo(): StateListRepo {
        return StateListRepo(StateListApi.create())
    }
}