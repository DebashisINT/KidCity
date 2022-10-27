package com.kcteam.features.myprofile.api

/**
 * Created by Pratishruti on 16-02-2018.
 */
object MyProfileRepoProvider {
    fun provideUpdateProfileRepo(): MyProfileRepository {
        return MyProfileRepository(MyProfileApi.create())
    }

    fun provideStateCityRepo(): MyProfileRepository {
        return MyProfileRepository(MyProfileApi.onlyCreate())
    }
}