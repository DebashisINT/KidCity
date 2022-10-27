package com.kcteam.features.weather.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class WeatherAPIResponse(
        @SerializedName("coord") val coord:Pair<Double,Double>,
        @SerializedName("weather") val weather:List<Weather>,
        @SerializedName("main") val main:Main,
        @SerializedName("base") val base:String,
        @SerializedName("visibility") val visibility:Long,
        @SerializedName("wind") val wind:Wind,
        @SerializedName("name") val locationName:String
):Serializable

data class ForeCastAPIResponse(
        @SerializedName("cnt") val cnt:Int,
        @SerializedName("message") val message:String,
        @SerializedName("list") val forecastList:List<ForeCast>
):Serializable

data class ForeCast(
        @SerializedName("dt") val date:Int,
        @SerializedName("main") val main:Main,
        @SerializedName("weather") val weather:List<Weather>,
        @SerializedName("wind") val wind:Wind,
        @SerializedName("dt_txt") val dateText:String
)

data class Weather(
        @SerializedName("id") val id:Int,
        @SerializedName("main") val main:String,
        @SerializedName("description") val description:String,
        @SerializedName("icon") val icon:String
)

data class Main(
        @SerializedName("temp") val temp:Double,
        @SerializedName("pressure") val pressure:Double,
        @SerializedName("humidity") val humidity:Double,
        @SerializedName("temp_min") val minTemp:Double,
        @SerializedName("temp_max") val maxTemp:Double,
        @SerializedName("sea_level") val seaLevel:Double,
        @SerializedName("grnd_level") val groundLevel:Double,
        @SerializedName("temp_kf") val temp_kf:Double
)

data class Wind (
        @SerializedName("speed") val speed:Double,
        @SerializedName("degree") val degree:Double
)