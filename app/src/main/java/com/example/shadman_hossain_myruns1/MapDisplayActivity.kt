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

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback{
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var stats: TextView
    private var typeOfActivityCode = -1
    private var typeOfActivityName: String? = null
    private var entryKey = -1
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



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_display)
        typeOfActivityCode = intent.getIntExtra("activityCode", -1)
        typeOfActivityName = intent.getStringExtra("activityName")
        entryKey = intent.getIntExtra("entryKey", -1)
        appContext = this.applicationContext
        getUnitType()
        Utilities.checkForGPSPermission(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        exerciseViewModelFactory = ExerciseViewModelFactory(repository)
        exerciseViewModel = ViewModelProvider(this, exerciseViewModelFactory).get(ExerciseViewModel::class.java)
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            finish()
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            removeMarkersAndPolylines()
            unBindService()
            stopService(serviceIntent)
            finish()
        }
        stats = findViewById(R.id.stats)
        mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mapViewModel.getActivityName(typeOfActivityName)
        mapViewModel.statsText.observe(this, {statsTV ->
            stats.text = statsTV
        })
        mapViewModel.markersLatlng.observe(this, { markerValues ->
            centerCamera(markerValues)
            setMarkerAndPolyline(markerValues)
        }
        )
        if (savedInstanceState != null){
            isBind = savedInstanceState.getBoolean(BIND_STATUS_KEY)
        }
        startTrackingService(typeOfActivityCode, type!!)

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
    }

    private fun startTrackingService(activityCode: Int, type: String){
        if (!isBind) {
            serviceIntent = Intent(this, TrackingService::class.java).apply {
                putExtra("activityCode", activityCode)
                putExtra("type", type)
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
        backPressedCallback.remove()
        unBindService()
        stopService(serviceIntent)
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
        polylineOptions = PolylineOptions()
        polylineList = ArrayList()
        locationList = ArrayList()
        markerOptions = MarkerOptions()
    }
    private fun centerCamera(latLng: LatLng){
        val updateCamera = CameraUpdateFactory.newLatLng(latLng)
        map.animateCamera(updateCamera)
    }

    private fun setMarkerAndPolyline(latLng: LatLng){
        if (polylineList.size == 0){
            startMarker = map.addMarker(MarkerOptions().position(latLng).title("Start"))
            startTimeMillis = System.currentTimeMillis()
        }
        else{
            currentMarker?.remove()
            currentMarker = map.addMarker(MarkerOptions().position(latLng))
        }
        polylineOptions.add(latLng)
        locationList.add(latLng)
        polylineList.add(map.addPolyline(polylineOptions))
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

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startTrackingService(typeOfActivityCode, type!!)
            }
        }
    }
}