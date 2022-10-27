package com.kcteam.features.weather.presentation

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kcteam.R
import com.kcteam.features.weather.model.ForeCast
import kotlinx.android.synthetic.main.inflate_forcast_item.view.*

class ForecastAdapter(private val context: Context) : RecyclerView.Adapter<ForecastAdapter.MyViewHolder>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private val dayList by lazy {
        ArrayList<String>()
    }

    private val forecastList by lazy {
        ArrayList<ForeCast>()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = layoutInflater.inflate(R.layout.inflate_forcast_item, parent, false)
        return MyViewHolder(v)
    }

    override fun getItemCount(): Int {
        return forecastList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems() {

            itemView.apply {
                tvTemp.text = forecastList[adapterPosition].main?.temp?.toInt().toString() + context.getString(R.string.degree) +
                        " / " + forecastList[adapterPosition].main?.maxTemp?.toInt().toString() + context.getString(R.string.degree)

                tvDay.text = dayList[adapterPosition]

                tvWeather.text = forecastList[adapterPosition].weather[0].description.toUpperCase()

                when {
                    tvWeather.text.contains("cloud", ignoreCase = true) -> ivWeather.setImageResource(R.drawable.ic_cloudy)
                    tvWeather.text.contains("haze", ignoreCase = true) -> ivWeather.setImageResource(R.drawable.ic_haze)
                    tvWeather.text.contains("rain", ignoreCase = true) -> ivWeather.setImageResource(R.drawable.ic_rain)
                    tvWeather.text.contains("thunderstorm", ignoreCase = true) -> ivWeather.setImageResource(R.drawable.ic_thunderstorm)
                    tvWeather.text.contains("clear", ignoreCase = true) -> ivWeather.setImageResource(R.drawable.ic_clear)
                }
            }
        }
    }

    fun refreshList(day: String?, foreCast: ForeCast) {
        if(!dayList.contains(day)) {
            forecastList.add(foreCast)
            dayList.add(day!!)
            notifyItemInserted(forecastList.size - 1)
        }
    }
}