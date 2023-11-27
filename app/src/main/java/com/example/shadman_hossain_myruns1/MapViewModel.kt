package com.example.shadman_hossain_myruns1

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MapViewModel : ViewModel(), ServiceConnection {
    private var avgSpeed: Double? = null
    private var currentSpeed: Double? = null
    private var climb: Double? = null
    private var calories: Double? = null
    private var distance: Double? = null
    private var type:String? = null
    private var startMarkerLatitude: Double? = null
    private var startMarkerLongitude: Double? = null
    private var currentMarkerLatitude: Double? = null
    private var currentMarkerLongitude: Double? = null
    private var typeOfActivityCode = -1
    private var typeOfActivityName: String? = null
    private val _statsText = MutableLiveData<String>()
    private val _locationMarkersLatlng = MutableLiveData<LatLng>()
    private var updateHandler: MyUpdateHandler
    private var binder: TrackingService.MyBinder? = null
    private var exerciseEntry: ExerciseEntry? = null
    private val _exerciseEntry = MutableLiveData<ExerciseEntry>()
    val exerciseEntryLiveData: LiveData<ExerciseEntry>
        get() = _exerciseEntry
    val statsText: LiveData<String>
        get() = _statsText

    val markersLatlng: LiveData<LatLng>
        get() = _locationMarkersLatlng

    init {
        updateHandler = MyUpdateHandler(Looper.getMainLooper())
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder?) {
        binder = service as TrackingService.MyBinder
        binder!!.setUpdateHandler(updateHandler)
    }
    override fun onServiceDisconnected(name: ComponentName) {
        binder = null
    }

    private fun updateStats(){
        _statsText.value = "Activity Type: $typeOfActivityName \n" +
                "Average Speed: $avgSpeed $type/h \n" +
                "Current Speed: $currentSpeed $type/h \n" +
                "Climb: $climb $type\n" +
                "Calories: $calories \n" +
                "Distance: $distance $type"
    }

    private fun updateMarkers(){
        _locationMarkersLatlng.value = LatLng(currentMarkerLatitude!!,currentMarkerLongitude!!)
    }

    private fun updateExerciseEntry(){
        _exerciseEntry.value = exerciseEntry!!
    }

    fun getActivityName(name: String?){
        typeOfActivityName = name
    }


    inner class MyUpdateHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            if (msg.what == TrackingService.UPDATE_INT_VALUE) {
                val bundle = msg.data
                typeOfActivityCode = bundle.getInt(TrackingService.typeOfActivityCodeKey)
                avgSpeed = bundle.getDouble(TrackingService.avgSpeedKey)
                currentSpeed = bundle.getDouble(TrackingService.currentSpeedKey)
                climb = bundle.getDouble(TrackingService.climbKey)
                calories = bundle.getDouble(TrackingService.caloriesKey)
                distance = bundle.getDouble(TrackingService.distanceKey)
                startMarkerLatitude = bundle.getDouble(TrackingService.startMarkerLatitudeKey)
                startMarkerLongitude = bundle.getDouble(TrackingService.startMarkerLongitudeKey)
                currentMarkerLatitude = bundle.getDouble(TrackingService.currentMarkerLatitudeKey)
                currentMarkerLongitude = bundle.getDouble(TrackingService.currentMarkerLongitudeKey)
                exerciseEntry = bundle.getParcelable(TrackingService.exerciseEntryKey)
                type = bundle.getString(TrackingService.typeKey)
                checkActivityType(typeOfActivityCode)
                updateStats()
                updateMarkers()
                updateExerciseEntry()
            }
        }
    }
    private fun checkActivityType(activityTypeCode: Int){
        if (activityTypeCode == 0){
            typeOfActivityName = "Runnning"
        }
        else if (activityTypeCode == 1){
            typeOfActivityName = "Walking"
        }
        else if (activityTypeCode == 2){
            typeOfActivityName = "Standing"
        }
        else if (activityTypeCode == 3){
            typeOfActivityName = "Cycling"
        }
        else if (activityTypeCode == 4){
            typeOfActivityName = "Hiking"
        }
        else if (activityTypeCode == 5){
            typeOfActivityName = "Downhill Skiing"
        }
        else if (activityTypeCode == 6){
            typeOfActivityName = "Cross-Country Skiing"
        }
        else if (activityTypeCode == 7){
            typeOfActivityName = "Snowboarding"
        }
        else if (activityTypeCode == 8){
            typeOfActivityName = "Skating"
        }
        else if (activityTypeCode == 9){
            typeOfActivityName = "Swimming"
        }
        else if (activityTypeCode == 10){
            typeOfActivityName = "Mountain Biking"
        }
        else if (activityTypeCode == 11){
            typeOfActivityName = "Wheelchair"
        }
        else if (activityTypeCode == 12){
            typeOfActivityName = "Elliptical"
        }
        else {
            typeOfActivityName = "Other"
        }
    }
}