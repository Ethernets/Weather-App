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

class WeatherAdapter: ListAdapter<WeatherModel, WeatherAdapter.WeatherViewHolder>(WeatherDiffUtil()) {
    class WeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ListItemBinding.bind(view)
        fun bind(weatherModel: WeatherModel) = with(binding) {
            tvDateItemList.text = weatherModel.time
            tvConditionListItem.text = weatherModel.condition
            tvTempListItem.text = weatherModel.currentTemp
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
        return WeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
