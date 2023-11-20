package com.example.shadman_hossain_myruns1

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.util.Calendar
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener{
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
    private var avgSpeed: Double? = null
    private lateinit var speedList: ArrayList<Double>
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
    private lateinit var locationList: ArrayList<LatLng>
    private lateinit var unitType: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_display)
        typeOfActivityCode = intent.getIntExtra("activityCode", -1)
        typeOfActivityName = intent.getStringExtra("activityName")
        entryKey = intent.getIntExtra("entryKey", -1)
        Utilities.checkForGPSPermission(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        speedList = ArrayList()
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            finish()
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            removeMarkersAndPolylines()
            finish()
        }
        stats = findViewById(R.id.stats)

    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        polylineOptions = PolylineOptions()
        polylineList = ArrayList()
        locationList = ArrayList()
        markerOptions = MarkerOptions()
        initializeLocationManager()
    }


    private fun initializeLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null)
                    onLocationChanged(location)

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
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
        Log.d("MapDisplayActivity", "Marker added at: $latLng")
    }

    private fun removeMarkersAndPolylines(){
        for(i in polylineList.indices){
            polylineList[i].remove()
        }
        polylineOptions.points.clear()
        map.clear()
    }

    override fun onLocationChanged(location: Location) {
        val latLng = getLatLang(location)
        centerCamera(latLng)
        setMarkerAndPolyline(latLng)
        getSpeedAndAddtoList(location)
        calculateAvgSpeed()
        getClimb(location)
        getDistance()
        getCalories()
        stats.setText("Activity Type: $typeOfActivityName \n" +
                "Average Speed: $avgSpeed \n" +
                "Current Speed: $currentSpeed \n" +
                "Climb: $climb \n" +
                "Calories: $calories \n" +
                "Distance: $distance")
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
        unitType = this.getSharedPreferences("Unit", AppCompatActivity.MODE_PRIVATE)
        var type = unitType.getString("unitType", "km")
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
                    * cos(Math.toRadians(currentMarker!!.position.latitude))) * sin(longitude/2) * sin(longitude/2)
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

    override fun onDestroy() {
        super.onDestroy()
        if(locationManager != null){
            locationManager.removeUpdates(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initializeLocationManager()
            }
        }
    }
}