package com.example.shadman_hossain_myruns1

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TypeConverter {
    @TypeConverter
    fun fromLatLngList(locationList: ArrayList<LatLng>?): String?{
        if (locationList == null || locationList.isEmpty()){
            return null
        }
        else{
            val gson = Gson()
            return gson.toJson(locationList)
        }
    }

    @TypeConverter
    fun toLatLngList(locationListString: String?): ArrayList<LatLng>?{
        if (locationListString == null || locationListString.isEmpty()){
            return null
        }
        else{
            val gson = Gson()
            val type = object : TypeToken<ArrayList<LatLng>>() {}.type
            return gson.fromJson(locationListString, type)
        }
    }
}