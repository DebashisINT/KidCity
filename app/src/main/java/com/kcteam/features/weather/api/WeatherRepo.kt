package com.kcteam.features.weather.api

import com.kcteam.base.BaseResponse
import com.kcteam.features.task.api.TaskApi
import com.kcteam.features.task.model.AddTaskInputModel
import com.kcteam.features.weather.model.ForeCastAPIResponse
import com.kcteam.features.weather.model.WeatherAPIResponse
import io.reactivex.Observable

class WeatherRepo(val apiService: WeatherApi) {
    fun getCurrentWeather(zipCode: String): Observable<WeatherAPIResponse> {
        return apiService.getTodayWeather(zipCode)
    }

    fun getWeatherForecast(zipCode: String): Observable<ForeCastAPIResponse> {
        return apiService.getForecast(zipCode)
    }
}