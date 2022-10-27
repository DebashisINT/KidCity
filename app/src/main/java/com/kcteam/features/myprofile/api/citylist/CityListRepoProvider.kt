package com.kcteam.features.myprofile.api.citylist

/**
 * Created by Pratishruti on 19-02-2018.
 */
object CityListRepoProvider {
    fun provideCityListRepo(): CityListRepo {
        return CityListRepo(CityListApi.create())
    }

}