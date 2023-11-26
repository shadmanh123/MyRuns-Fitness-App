package com.example.shadman_hossain_myruns1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import java.util.Calendar
import java.util.Timer
import java.util.TimerTask
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class TrackingService: android.app.Service(), LocationListener {
    private lateinit var myBinder: Binder
    private lateinit var notificationManager: NotificationManager
    private var isTracking = true
    private lateinit var pendingIntent: PendingIntent
    private var updateHandler: Handler? = null
    private lateinit var myTask: MyTask
    private lateinit var locationManager: LocationManager
    private lateinit var locationList: ArrayList<LatLng>
    private var speedList: ArrayList<Double> = ArrayList()
    private var avgSpeed: Double? = null
    private var currentSpeed: Double? = null
    private var startAltitude: Double? = null
    private var currentAltitude: Double? = null
    private var climb: Double? = null
    private var calories: Double? = null
    private var met: Double? = null
    private var distance: Double? = null
    private var dateTime: Long? = null
    private var duration: Double? = null
    private var startTimeMillis: Long? = null
    private var currentTimeMillis: Long? = null
    private var avgPace: Double? = null
    private var heartRate: Double? = null
    private var type:String? = null
    private var startMarkerPosition: LatLng? = null
    private var startMarkerLatitude: Double? = null
    private var startMarkerLongitude: Double? = null
    private var currentMarkerPosition: LatLng? = null
    private var currentMarkerLatitude: Double? = null
    private var currentMarkerLongitude: Double? = null
    private var typeOfActivityCode = -1
    private var typeOfInputValue = -1
    private lateinit var timer: Timer
    private var exerciseEntry: ExerciseEntry? = null

    companion object{
        const val startMarkerLatitudeKey = "startMarkerLatitudeKey"
        const val startMarkerLongitudeKey = "startMarkerLongitudeKey"
        const val currentMarkerLatitudeKey = "currentMarkerLatitudeKey"
        const val currentMarkerLongitudeKey = "currentMarkerLongitudeKey"
        const val avgSpeedKey = "avgSpeedKey"
        const val currentSpeedKey = "currentSpeedKey"
        const val climbKey = "climbKey"
        const val caloriesKey = "caloriesKey"
        const val distanceKey = "distanceKey"
        const val UPDATE_INT_VALUE = 0
        const val STOP_TRACKING_SERVICE = "stop tracking service"
        const val NOTIFY_ID = 11
        const val CHANNEL_ID = "notification channel"
        const val exerciseEntryKey = "exerciseEntryKey"
        const val typeKey = "typeKey"
    }

    override fun onCreate() {
        super.onCreate()
        myTask = MyTask()
        timer = Timer()
        locationList = ArrayList()
        timer.scheduleAtFixedRate(myTask, 0, 1000L)
        myBinder = MyBinder()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.hasExtra("activityCode") && intent.hasExtra("type")){
            typeOfActivityCode = intent.getIntExtra("activityCode", -1)
            type = intent.getStringExtra("type")
            typeOfInputValue = intent.getIntExtra("inputTypeValue", -1)
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun setUpdateHandler(updateHandler: Handler) {
            this@TrackingService.updateHandler = updateHandler
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        updateHandler = null
        return true
    }

    override fun onDestroy() {
        isTracking = false
        cleanUpTasks()
        stopSelf()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        cleanUpTasks()
        stopSelf()
    }

    private fun cleanUpTasks() {
        notificationManager.cancel(NOTIFY_ID)
        if(locationManager != null){
            locationManager.removeUpdates(this)
        }
        if(timer != null){
            timer.cancel()
        }
        avgSpeed = null
        currentSpeed = null
        speedList.clear()
        startAltitude = null
        currentAltitude = null
        climb = null
        calories = null
        met = null
        distance = null
//        dateTime: Calendar
        duration  = null
        startTimeMillis = null
        currentTimeMillis = null
        avgPace = null
        heartRate = null
        type = null
        startMarkerPosition = null
        currentMarkerPosition = null
        typeOfActivityCode = -1
    }

    private fun showNotification(){
        val intent = Intent(this, MapDisplayActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
        notificationBuilder.setContentTitle("MyRuns4 tracking location")
        notificationBuilder.setContentText("Tap to view workout progress")
        notificationBuilder.setSmallIcon(R.drawable.heart)
        notificationBuilder.setContentIntent(pendingIntent)
        val notification = notificationBuilder.build()
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                "channel name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFY_ID, notification)
    }
    inner class MyTask : TimerTask() {
        override fun run() {
            try {
                if (isTracking == false){
                    cleanUpTasks()
                    stopSelf()
                }
                initializeLocationManager()
                updateExerciseEntry()

                if(updateHandler != null){
                    val bundle = Bundle()
                    bundle.putDouble(avgSpeedKey, avgSpeed!!)
                    bundle.putDouble(currentSpeedKey,currentSpeed!!)
                    bundle.putDouble(climbKey, climb!!)
                    bundle.putDouble(caloriesKey,calories!!)
                    bundle.putDouble(distanceKey,distance!!)
                    bundle.putDouble(startMarkerLatitudeKey, startMarkerLatitude!!)
                    bundle.putDouble(startMarkerLongitudeKey, startMarkerLongitude!!)
                    bundle.putDouble(currentMarkerLatitudeKey, currentMarkerLatitude!!)
                    bundle.putDouble(currentMarkerLongitudeKey, currentMarkerLongitude!!)
                    bundle.putParcelable(exerciseEntryKey, exerciseEntry)
                    bundle.putString(typeKey, type)
                    val update = updateHandler!!.obtainMessage()
                    update.data = bundle
                    update.what = UPDATE_INT_VALUE
                    updateHandler!!.sendMessage(update)
                }
            } catch (t: Throwable) {
                println("debug: Timer Tick Failed. $t")
//                stopSelf()
            }
        }
    }
    private fun initializeLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null)
                    onLocationChanged(location)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0f,pendingIntent)
            }
            return
        }
        catch (e: SecurityException){
            Toast.makeText(this, getString(R.string.grant_permission_message), Toast.LENGTH_LONG).show()
        }
    }

    private fun getLatLang(location: Location): LatLng {
        val latitude = location.latitude
        val longitude = location.longitude
        return LatLng(latitude,longitude)
    }

    private fun getLatLangSeperate(location: Location){
        if(startMarkerPosition  == null){
            startMarkerLatitude = location.latitude
            startMarkerLongitude = location.longitude
            currentMarkerLatitude = startMarkerLatitude
            currentMarkerLongitude = startMarkerLongitude
        }
        else{
            currentMarkerLatitude = location.latitude
            currentMarkerLongitude = location.longitude
        }
    }

    override fun onLocationChanged(location: Location) {
        getMarkers(location)
        getSpeedAndAddtoList(location)
        calculateAvgSpeed()
        getClimb(location)
        getDistance()
        getCalories()
    }

    private fun getMarkers(location: Location){
        val latLng = getLatLang(location)
        locationList.add(latLng)
        getLatLangSeperate(location)
        if (startMarkerPosition == null){
            startMarkerPosition = latLng
        }
        else{
            currentMarkerPosition = latLng
        }

    }
    private fun getSpeedAndAddtoList(location: Location){
        currentSpeed = location.speed.toDouble()
        speedList.add(currentSpeed!!)
    }

    private fun calculateAvgSpeed(){
        avgSpeed = speedList.average()
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
        if (currentMarkerPosition != null) {
            val earthRadius = getEarthRadius()

            val latitude = Math.toRadians(currentMarkerPosition!!.latitude - startMarkerPosition!!.latitude)
            val longitude = Math.toRadians(currentMarkerPosition!!.longitude - startMarkerPosition!!.longitude)
            val a = sin(latitude/2) * sin(latitude/2) + cos(Math.toRadians(startMarkerPosition!!.latitude)
                    * cos(Math.toRadians(currentMarkerPosition!!.latitude))
            ) * sin(longitude/2) * sin(longitude/2)
            val notA = 1 - a
            val c = 2 * atan2(sqrt(a), sqrt(notA))
            distance = earthRadius * c
        }
        else{
            distance = 0.0
        }
    }

    private fun getCalories(){
        val constantForkCal = 1.05
        val avgWeightOnEarth = 136.7
        checkMetValue(typeOfActivityCode)
        duration = getDuration()
        calories = met!! * duration!! * avgWeightOnEarth * constantForkCal
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
    private fun updateExerciseEntry(){
        getDateTime()
        if(exerciseEntry == null) {
            exerciseEntry = ExerciseEntry(
                inputType = typeOfInputValue,
                activityType = typeOfActivityCode,
                dateTime = dateTime,
                duration = duration,
                distance = distance,
                distanceUnit = type,
                avgSpeed = avgSpeed,
                calorie = calories,
                climb = climb,
                locationList = locationList
            )
        }
        else{
            exerciseEntry?.apply {
                this.duration = duration
                this.distance = distance
                this.avgSpeed = avgSpeed
                this.calorie = calorie
                this.climb = climb
                this.locationList = locationList
            }
        }
    }
    private fun getDateTime() {
        val dateCalendar = Calendar.getInstance()
//        dateCalendar.timeInMillis = date!!
        val timeCalendar = Calendar.getInstance()
//        timeCalendar.timeInMillis = time!!
        val year = dateCalendar.get(Calendar.YEAR)
        val month = dateCalendar.get(Calendar.MONTH)
        val day = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = timeCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = timeCalendar.get(Calendar.MINUTE)
        val seconds = timeCalendar.get(Calendar.SECOND)
        val dateTimeCalendar = Calendar.getInstance()
        dateTimeCalendar.set(year, month, day, hour, minute, seconds)
        dateTime = dateTimeCalendar.timeInMillis
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