package com.example.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.weatherapp.Constants
import com.example.weatherapp.MainViewModel
import com.example.weatherapp.R
import com.example.weatherapp.adapters.ViewPagerAdapter
import com.example.weatherapp.databinding.FragmentMainBinding
import com.example.weatherapp.models.WeatherModel
import com.google.android.material.tabs.TabLayoutMediator
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainFragment : Fragment() {
    private lateinit var pLauncher: ActivityResultLauncher<String>
    private lateinit var binding: FragmentMainBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
        initViewPager()
        requestWeatherData("London")
        updateCurrentCard()
    }

    private fun initViewPager(){
        val listFragment = listOf(HoursFragment.newInstance(), DaysFragment.newInstance())
        val adapter = ViewPagerAdapter(requireActivity(), listFragment)
        binding.viewPagerMain.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerMain){
            tab, position ->
            when(position){
                0 -> tab.text = "Hours"
                1 -> tab.text = "Days"
            }
        }.attach()
    }

    private fun permissionLauncher() {
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (!isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            permissionLauncher()
            pLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestWeatherData(city: String) {
        val url = Constants.BASE_URL + "?key=" + Constants.API_KEY + "&q=" + city + "&days=3"
        val queue = Volley.newRequestQueue(requireContext())
        val request = StringRequest(Request.Method.GET, url, { result ->
            parseWeatherData(result)
        }, {
            Log.e("MainFragment", it.message.toString())
        })
        queue.add(request)
    }

    private fun parseWeatherData(result: String) {
        val mainObject = JSONObject(result)
        val days =  parseDays(mainObject)
        parseCurrentDate(mainObject, days[0])

    }

    private fun parseCurrentDate(mainObject: JSONObject, weatherModel: WeatherModel) {
        val item = WeatherModel(
            mainObject.getJSONObject("location").getString("name"),
            mainObject.getJSONObject("current").getString("last_updated"),
            mainObject.getJSONObject("current").getJSONObject("condition").getString("text"),
            mainObject.getJSONObject("current").getString("temp_c"),
            weatherModel.maxTemp,
            weatherModel.minTemp,
            mainObject.getJSONObject("current").getJSONObject("condition").getString("icon"),
            weatherModel.hours,
        )
        model.liveDataCurrent.value = item
    }

    private fun parseDays(mainObject: JSONObject): List<WeatherModel>{
        val list = ArrayList<WeatherModel>()
        val daysArray = mainObject.getJSONObject("forecast").getJSONArray("forecastday")
        val name = mainObject.getJSONObject("location").getString("name")
        for (i in 0 until daysArray.length()){
            val day = daysArray[i] as JSONObject
            val item = WeatherModel(
                name,
                day.getString("date"),
                day.getJSONObject("day").getJSONObject("condition").getString("text"),
                "",
                day.getJSONObject("day").getString("maxtemp_c"),
                day.getJSONObject("day").getString("mintemp_c"),
                day.getJSONObject("day").getJSONObject("condition").getString("icon"),
                day.getJSONArray("hour").toString(),
            )
            list.add(item)
        }
        return list
    }

    private fun updateCurrentCard() = with(binding){
        model.liveDataCurrent.observe(viewLifecycleOwner) {
            tvDate.text = it.time
            tvCity.text = it.city
            tvCurrentTemp.text = it.currentTemp
            tvCondition.text = it.condition
            tvMaxMin.text = "${it.maxTemp}C / ${it.minTemp}C"
            Picasso.get().load("https:" + it.imageUrl).into(ivWeather)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }
}