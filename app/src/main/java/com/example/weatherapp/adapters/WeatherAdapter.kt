package com.example.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ListItemBinding
import com.example.weatherapp.models.WeatherModel
import com.squareup.picasso.Picasso

class WeatherAdapter(val listener: Listener?): ListAdapter<WeatherModel, WeatherAdapter.WeatherViewHolder>(WeatherDiffUtil()) {
    class WeatherViewHolder(view: View, val listener: Listener?) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemBinding.bind(view)
        var itemTemp: WeatherModel? = null
        init {
            view.setOnClickListener {
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }
        fun bind(weatherModel: WeatherModel) = with(binding) {
            itemTemp = weatherModel
            tvDateItemList.text = weatherModel.time
            tvConditionListItem.text = weatherModel.condition
            tvTempListItem.text = weatherModel.currentTemp.ifEmpty { "${weatherModel.maxTemp}°C / ${weatherModel.minTemp}°C" }
            Picasso.get().load("https:" + weatherModel.imageUrl).into(ivListItem)
        }
    }

    class WeatherDiffUtil : DiffUtil.ItemCallback<WeatherModel>() {
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return WeatherViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener {
        fun onClick(weatherModel: WeatherModel)
    }
}
