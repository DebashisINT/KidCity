package com.kcteam.features.weather.api

import com.kcteam.features.task.api.TaskApi
import com.kcteam.features.task.api.TaskRepo

object WeatherRepoProvider {
    fun weatherRepoProvider(): WeatherRepo {
        return WeatherRepo(WeatherApi.create())
    }
}