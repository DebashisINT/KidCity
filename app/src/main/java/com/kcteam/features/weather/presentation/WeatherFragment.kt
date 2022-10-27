package com.kcteam.features.weather.presentation

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import com.kcteam.R
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.base.presentation.BaseFragment
import com.kcteam.features.SearchLocation.locationInfoModel
import com.kcteam.features.dashboard.presentation.DashboardActivity
import com.kcteam.features.location.LocationWizard
import com.kcteam.features.weather.api.WeatherRepoProvider
import com.kcteam.features.weather.model.WeatherAPIResponse
import com.kcteam.widgets.AppCustomTextView
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class WeatherFragment : BaseFragment() {

    private lateinit var mContext: Context

    private lateinit var tv_temp: AppCustomTextView
    private lateinit var tv_weather_type: AppCustomTextView
    private lateinit var tv_humidity: AppCustomTextView
    private lateinit var tv_location: AppCustomTextView
    private lateinit var iv_weather_pic: ImageView
    private lateinit var progress_wheel: ProgressWheel
    private lateinit var tv_no_data_available: AppCustomTextView
    private lateinit var rv_forcast_list: RecyclerView
    private lateinit var rl_weather_body: RelativeLayout
    private lateinit var rl_weather_main: RelativeLayout

    private var zipCode = ""
    private val forecastAdapter by lazy {
        ForecastAdapter(mContext)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        initView(view)
        getWeatherData()

        return view
    }

    private fun initView(view: View) {
        view.apply {
            tv_temp = findViewById(R.id.tv_temp)
            tv_weather_type = findViewById(R.id.tv_weather_type)
            tv_humidity = findViewById(R.id.tv_humidity)
            tv_location = findViewById(R.id.tv_location)
            iv_weather_pic = findViewById(R.id.iv_weather_pic)
            progress_wheel = findViewById(R.id.progress_wheel)
            tv_no_data_available = findViewById(R.id.tv_no_data_available)
            rv_forcast_list = findViewById(R.id.rv_forcast_list)
            rl_weather_body = findViewById(R.id.rl_weather_body)
            rl_weather_main = findViewById(R.id.rl_weather_main)
        }

        rl_weather_main.setOnClickListener(null)
        progress_wheel.stopSpinning()
        rv_forcast_list.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        rv_forcast_list.adapter = forecastAdapter

        zipCode = LocationWizard.getPostalCode(mContext, Pref.current_latitude.toDouble(), Pref.current_longitude.toDouble()) + ",IN"
    }

    private fun getWeatherData() {
        if (!AppUtils.isOnline(mContext)) {
            (mContext as DashboardActivity).showSnackMessage(getString(R.string.no_internet))
            rl_weather_body.visibility = View.GONE
            tv_no_data_available.visibility = View.VISIBLE
            return
        }

        progress_wheel.spin()

        val repository = WeatherRepoProvider.weatherRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getCurrentWeather(zipCode)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as WeatherAPIResponse

                            rl_weather_body.visibility = View.VISIBLE
                            tv_no_data_available.visibility = View.GONE

                            response?.apply {
                                tv_temp.text = main.temp.toInt().toString() + getString(R.string.degree) + "C"
                                tv_weather_type.text = weather[0].description.capitalize()
                                tv_humidity.text = "H:${main.humidity.toInt()}% P:${main.pressure.toInt()} mb"
                                tv_location.text = locationName

                                when {
                                    tv_weather_type.text.contains("cloud", ignoreCase = true) -> iv_weather_pic.setImageResource(R.drawable.ic_cloudy)
                                    tv_weather_type.text.contains("haze", ignoreCase = true) -> iv_weather_pic.setImageResource(R.drawable.ic_haze)
                                    tv_weather_type.text.contains("rain", ignoreCase = true) -> iv_weather_pic.setImageResource(R.drawable.ic_rain)
                                    tv_weather_type.text.contains("thunderstorm", ignoreCase = true) -> iv_weather_pic.setImageResource(R.drawable.ic_thunderstorm)
                                    tv_weather_type.text.contains("clear", ignoreCase = true) -> iv_weather_pic.setImageResource(R.drawable.ic_clear)
                                }
                            }

                            getForecast()
                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Can not get Weather Info.")
                            rl_weather_body.visibility = View.GONE
                            tv_no_data_available.visibility = View.VISIBLE
                        })
        )
    }

    private fun getForecast() {
        val repository = WeatherRepoProvider.weatherRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.getWeatherForecast(zipCode)
                        .flatMap {
                            Observable.fromIterable(it.forecastList)
                        }
                        .groupBy {
                            AppUtils.getDayFromEmptyDateTimeStamp(it.dateText)
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            progress_wheel.stopSpinning()
                            /*val response = result as ForeCastAPIResponse
                            if (response.forecastList != null && response.forecastList.isNotEmpty())
                                initAdapter(response.forecastList)
                            else {
                                (mContext as DashboardActivity).showSnackMessage("Can not get Weather forecast")
                                rv_forcast_list.visibility = View.GONE
                            }*/
                            rv_forcast_list.visibility = View.VISIBLE
                            forecastAdapter.refreshList(it.key, it.blockingFirst())

                        }, { error ->
                            progress_wheel.stopSpinning()
                            error.printStackTrace()
                            (mContext as DashboardActivity).showSnackMessage("Can not get Weather forecast")
                            rv_forcast_list.visibility = View.GONE
                        })
        )
    }

    fun getLocationFromMap(locationInfoModel: locationInfoModel?) {
        if (!TextUtils.isEmpty(locationInfoModel?.pinCode)) {
            zipCode = locationInfoModel?.pinCode!! + ",IN"
            getWeatherData()
        }
    }
}