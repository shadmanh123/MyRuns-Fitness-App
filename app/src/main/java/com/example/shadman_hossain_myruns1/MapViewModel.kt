package com.example.shadman_hossain_myruns1

import android.content.ComponentName
import android.content.ServiceConnection
import android.location.Location
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar

class MapViewModel(): ViewModel(), ServiceConnection {
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
    private var startMarkerLatLng: LatLng? = null
    private var startMarkerLatitude: Double? = null
    private var startMarkerLongitude: Double? = null
    private var currentMarkerLatLng: LatLng? = null
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
                "Average Speed: $avgSpeed \n" +
                "Current Speed: $currentSpeed \n" +
                "Climb: $climb \n" +
                "Calories: $calories \n" +
                "Distance: $distance"
    }

    private fun updateMarkers(){
        _locationMarkersLatlng.value = LatLng(currentMarkerLatitude!!,currentMarkerLongitude!!)
    }

    fun getActivityName(name: String?){
        typeOfActivityName = name
    }


    inner class MyUpdateHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            if (msg.what == TrackingService.UPDATE_INT_VALUE) {
                val bundle = msg.data
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
                updateStats()
                updateMarkers()
            }
        }
    }
}