package com.example.shadman_hossain_myruns1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.util.concurrent.CompletableFuture

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var saveOrDeleteButton: Button
    private lateinit var cancelButton: Button
    private lateinit var stats: TextView
    private var typeOfActivityCode = -1
    private var typeOfActivityName: String? = null
    private var typeOfInputValue = -1
    private var entryKey = -1L
    private lateinit var locationManager: LocationManager
    private lateinit var map: GoogleMap
    private lateinit var markerOptions: MarkerOptions
    private var startMarker: Marker? = null
    private var currentMarker: Marker? = null
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var polylineList: ArrayList<Polyline>
    private var startTimeMillis: Long? = null
    private lateinit var locationList: ArrayList<LatLng>
    private lateinit var unitType: SharedPreferences
    private var type: String? = null
    private lateinit var mapViewModel: MapViewModel
    private lateinit var serviceIntent: Intent
    private var isBind = false
    private val BIND_STATUS_KEY = "bind_status_key"
    private lateinit var backPressedCallback: OnBackPressedCallback
    private lateinit var appContext: Context
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var repository: ExerciseRepository
    private lateinit var exerciseViewModelFactory: ExerciseViewModelFactory
    private lateinit var exerciseViewModel: ExerciseViewModel
    private var exerciseEntry: ExerciseEntry? = null
    private var activityToDisplay: ExerciseEntry? = null
    private lateinit var activity: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_display)
        entryKey = intent.getLongExtra("entryKey", -1L)
        appContext = this.applicationContext
        getUnitType()
        Utilities.checkForGPSPermission(this)
        stats = findViewById(R.id.stats)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        establishExerciseDatabase()
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)

        saveOrDeleteButton = findViewById(R.id.saveButton)
        cancelButton = findViewById(R.id.cancelButton)
        buttonFunctionality()
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                println("debug: back button pressed")
                unBindService()
                stopService(serviceIntent)
                isEnabled = false
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        typeOfInputValue = intent.getIntExtra("inputTypeValue", -1)
        if (typeOfInputValue == 3) {
            automaticModeActivities(savedInstanceState)
        }
        else{
            gpsModeActivities(savedInstanceState)
        }
    }

    private fun buttonFunctionality() {
        if (entryKey != -1L) {
            saveOrDeleteButton.text = "Delete"
            saveOrDeleteButton.setOnClickListener {
                exerciseViewModel.delete(entryKey)
                finish()
            }

            cancelButton.setOnClickListener {
                finish()
            }
        }
        else{
            saveOrDeleteButton.setOnClickListener {
                if (exerciseEntry == null) {
                    finish()
                } else {
                    saveToExerciseDatabase()
                    finish()
                }
            }
            cancelButton.setOnClickListener {
                removeMarkersAndPolylines()
                unBindService()
                stopService(serviceIntent)
                finish()
            }
        }
    }

    private fun automaticModeActivities(savedInstanceState: Bundle?) {
        mapViewModel.getActivityName("Deciding")
        observeMapViewModel()
        if (savedInstanceState != null) {
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }
        startTrackingService(typeOfActivityCode, type!!, typeOfInputValue)
    }

    private fun gpsModeActivities(savedInstanceState: Bundle?) {
        typeOfActivityCode = intent.getIntExtra("activityCode", -1)
        typeOfActivityName = intent.getStringExtra("activityName")
        mapViewModel.getActivityName(typeOfActivityName)
        observeMapViewModel()
        if (savedInstanceState != null) {
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }
        startTrackingService(typeOfActivityCode, type!!, typeOfInputValue)
    }

    private fun loadExerciseEntry() {
        activityToDisplay = getActivitytoDisplay(entryKey)
        val activityResult = checkActivityType(activityToDisplay!!.activityType!!.toInt())
        stats.text = "Activity Type: $activityResult \n" +
                "Average Speed: ${activityToDisplay!!.avgSpeed} ${activityToDisplay!!.distanceUnit}/h \n" +
                "Current Speed: 0 ${activityToDisplay!!.distanceUnit}/h \n" +
                "Climb: ${activityToDisplay!!.climb} ${activityToDisplay!!.distanceUnit}\n" +
                "Calories: ${activityToDisplay!!.calorie} \n" +
                "Distance: ${activityToDisplay!!.distance} ${activityToDisplay!!.distanceUnit}"
        locationList = activityToDisplay!!.locationList!!
        for (item in locationList) {
            centerCamera(item)
            setMarkerAndPolyline(item)
        }
    }

    private fun establishExerciseDatabase() {
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        exerciseViewModelFactory = ExerciseViewModelFactory(repository)
        exerciseViewModel =
            ViewModelProvider(this, exerciseViewModelFactory).get(ExerciseViewModel::class.java)
    }

    private fun getActivitytoDisplay(entryKey: Long): ExerciseEntry?{
        val exerciseActivityFuture = CompletableFuture<ExerciseEntry>()
        Thread{
            val threadExerciseActivity = exerciseViewModel.getEntryByID(entryKey) as ExerciseEntry
            exerciseActivityFuture.complete(threadExerciseActivity)
        }.start()
        return exerciseActivityFuture.get()
    }

    private fun observeMapViewModel() {
        mapViewModel.statsText.observe(this, { statsTV ->
            stats.text = statsTV
        })
        mapViewModel.markersLatlng.observe(this, { markerValues ->
            centerCamera(markerValues)
            setMarkerAndPolyline(markerValues)
        }
        )
        mapViewModel.exerciseEntryLiveData.observe(this, { entry ->
            exerciseEntry = entry
        })
    }

    private fun startTrackingService(activityCode: Int, type: String, inputTypeValue: Int){
        if (!isBind) {
            serviceIntent = Intent(this, TrackingService::class.java).apply {
                putExtra("activityCode", activityCode)
                putExtra("type", type)
                putExtra("inputTypeValue", inputTypeValue)
            }
            bindService()
            startService(serviceIntent)
        }
    }

    private fun bindService() {
        if (!isBind) {
            appContext.bindService(serviceIntent, mapViewModel, BIND_AUTO_CREATE)
            isBind = true
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (entryKey == -1L) {
            backPressedCallback.remove()
            unBindService()
            stopService(serviceIntent)
        }
    }

    private fun unBindService() {
        if (isBind) {
            appContext.unbindService(mapViewModel)
            isBind = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(BIND_STATUS_KEY, isBind)
    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        if(entryKey != -1L){
            polylineOptions = PolylineOptions()
            polylineList = ArrayList()
            markerOptions = MarkerOptions()
            loadExerciseEntry()
        }
        else {
            polylineOptions = PolylineOptions()
            polylineList = ArrayList()
            locationList = ArrayList()
            markerOptions = MarkerOptions()
        }
    }
    private fun centerCamera(latLng: LatLng){
        val updateCamera = CameraUpdateFactory.newLatLng(latLng)
        map.animateCamera(updateCamera)
    }

    private fun setMarkerAndPolyline(latLng: LatLng){
        if (polylineList.size == 0){
            startMarker = map.addMarker(MarkerOptions().position(latLng).title("Start"))
            startTimeMillis = System.currentTimeMillis()
            polylineOptions.add(latLng)
            polylineList.add(map.addPolyline(polylineOptions))
            if (entryKey == -1L){
                locationList.add(latLng)
            }
        }
        else{
            currentMarker?.remove()
            currentMarker = map.addMarker(MarkerOptions().position(latLng))
            polylineOptions.add(latLng)
            polylineList.add(map.addPolyline(polylineOptions))
            if (entryKey == -1L){
                locationList.add(latLng)
            }
        }

    }

    private fun removeMarkersAndPolylines(){
        for(i in polylineList.indices){
            polylineList[i].remove()
        }
        polylineOptions.points.clear()
        map.clear()
    }

    private fun getUnitType(){
        unitType = this.getSharedPreferences("Unit", AppCompatActivity.MODE_PRIVATE)
        type = unitType.getString("unitType", "km")
    }

    private fun saveToExerciseDatabase(){
//        Log.d("MapDisplayActivity", "Save exercise to db")
        exerciseViewModel.insert(exerciseEntry!!)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startTrackingService(typeOfActivityCode, type!!, typeOfInputValue)
            }
        }
    }

    private fun checkActivityType(activityTypeCode: Int): String {
        if (activityTypeCode == 0){
            activity = "Runnning"
        }
        else if (activityTypeCode == 1){
            activity = "Walking"
        }
        else if (activityTypeCode == 2){
            activity = "Standing"
        }
        else if (activityTypeCode == 3){
            activity = "Cycling"
        }
        else if (activityTypeCode == 4){
            activity = "Hiking"
        }
        else if (activityTypeCode == 5){
            activity = "Downhill Skiing"
        }
        else if (activityTypeCode == 6){
            activity = "Cross-Country Skiing"
        }
        else if (activityTypeCode == 7){
            activity = "Snowboarding"
        }
        else if (activityTypeCode == 8){
            activity = "Skating"
        }
        else if (activityTypeCode == 9){
            activity = "Swimming"
        }
        else if (activityTypeCode == 10){
            activity = "Mountain Biking"
        }
        else if (activityTypeCode == 11){
            activity = "Wheelchair"
        }
        else if (activityTypeCode == 12){
            activity = "Elliptical"
        }
        else if (activityTypeCode == 13){
            activity = "Other"
        }
        return activity
    }
}