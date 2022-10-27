package com.kcteam.features.myprofile.api.citylist

import com.kcteam.features.myprofile.model.citylist.CityListApiResponse
import io.reactivex.Observable

/**
 * Created by Pratishruti on 19-02-2018.
 */
class CityListRepo (val apiService: CityListApi) {
    fun getCityList(): Observable<CityListApiResponse> {
        return apiService.getAllCities()
    }
}