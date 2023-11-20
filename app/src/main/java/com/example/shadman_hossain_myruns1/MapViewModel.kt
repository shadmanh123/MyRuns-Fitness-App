package com.example.shadman_hossain_myruns1

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.Marker
import java.util.Calendar
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapViewModel(): ViewModel() {
    private lateinit var location: Location
    private var speedList: ArrayList<Double> = ArrayList()
    private var avgSpeed: Double? = null
    private var currentSpeed: Double? = null
    private var startAltitude: Double? = null
    private var currentAltitude: Double? = null
    private var climb: Double? = null
    private var calories: Double? = null
    private var met: Double? = null
    private var distance: Double? = null
    private lateinit var dateTime: Calendar
    private var duration: Double? = null
    private var startTimeMillis: Long? = null
    private var currentTimeMillis: Long? = null
    private var avgPace: Double? = null
    private var heartRate: Double? = null
    private var type:String? = null
    private var startMarker: Marker? = null
    private var currentMarker: Marker? = null
    private var typeOfActivityCode = -1
    private var typeOfActivityName: String? = null
    private val _statsText = MutableLiveData<String>()
    val statsText: LiveData<String>
        get() = _statsText

    private fun updateStats(){
        _statsText.value = "Activity Type: $typeOfActivityName \n" +
                "Average Speed: $avgSpeed \n" +
                "Current Speed: $currentSpeed \n" +
                "Climb: $climb \n" +
                "Calories: $calories \n" +
                "Distance: $distance"
    }

    fun getLocation(locationData: Location){
        location = locationData
        getSpeedAndAddtoList(location)
        calculateAvgSpeed()
        getClimb(location)
        getDistance()
        getCalories()
    }
    fun getMarkers(initialMarker: Marker?, endMarker: Marker?){
        if (initialMarker != null) {
            startMarker = initialMarker
        }
        if(endMarker != null) {
            currentMarker = endMarker
        }
    }

    fun getActivityCode(int: Int){
        typeOfActivityCode = int
    }

    fun getActivityName(string: String?){
        typeOfActivityName = string
    }

    fun getType(string: String?){
        type = string
    }
    private fun getSpeedAndAddtoList(location: Location){
        currentSpeed = location.speed.toDouble()
        speedList.add(currentSpeed!!)
        updateStats()
    }

    private fun calculateAvgSpeed(){
        avgSpeed = speedList.average()
        updateStats()
    }

    private fun getClimb(location: Location){
        if(startAltitude == null){
            startAltitude = location.altitude
            climb = startAltitude
        }
        else{
            currentAltitude = location.altitude
            climb = currentAltitude!! - startAltitude!!
        }
        updateStats()
    }

    private fun getEarthRadius(): Double{
        if (type == "miles"){
            return 3958.8
        }
        else{
            return 6378.1
        }
    }

    private fun getDistance(){ //using Haversine Formula
        if (currentMarker != null) {
            val earthRadius = getEarthRadius()

            val latitude = Math.toRadians(currentMarker!!.position.latitude - startMarker!!.position.latitude)
            val longitude = Math.toRadians(currentMarker!!.position.longitude - startMarker!!.position.longitude)
            val a = sin(latitude/2) * sin(latitude/2) + cos(Math.toRadians(startMarker!!.position.latitude)
                    * cos(Math.toRadians(currentMarker!!.position.latitude))
            ) * sin(longitude/2) * sin(longitude/2)
            val notA = 1 - a
            val c = 2 * atan2(sqrt(a), sqrt(notA))
            distance = earthRadius * c
        }
        else{
            distance = 0.0
        }
        updateStats()
    }

    private fun getCalories(){
        val constantForkCal = 1.05
        val avgWeightOnEarth = 136.7
        checkMetValue(typeOfActivityCode)
        duration = getDuration()
        calories = met!! * duration!! * avgWeightOnEarth * constantForkCal
        updateStats()
    }

    private fun getDuration(): Double{
        if(startTimeMillis != null){
            currentTimeMillis = System.currentTimeMillis()
            return (currentTimeMillis!! - startTimeMillis!!).toDouble()
        }
        else{
            return 0.00
        }
    }

    private fun checkMetValue(typeOfActivityCode: Int){
        if (typeOfActivityCode == 0){
            met = 9.80
        }
        else if (typeOfActivityCode == 1){
            met = 3.80
        }
        else if (typeOfActivityCode == 2){
            met = 1.59
        }
        else if (typeOfActivityCode == 3){
            met = 6.80
        }
        else if (typeOfActivityCode == 4){
            met = 7.30
        }
        else if (typeOfActivityCode == 5){
            met = 4.00
        }
        else if (typeOfActivityCode == 6){
            met = 5.90
        }
        else if (typeOfActivityCode == 7){
            met = 4.00
        }
        else if (typeOfActivityCode == 8){
            met = 7.00
        }
        else if (typeOfActivityCode == 9){
            met = 9.80
        }
        else if (typeOfActivityCode == 10){
            met = 14.00
        }
        else if (typeOfActivityCode == 11){
            met = 8.00
        }
        else if (typeOfActivityCode == 12){
            met = 5.00
        }
        else if (typeOfActivityCode == 13){
            met = 8.50
        }
    }
}