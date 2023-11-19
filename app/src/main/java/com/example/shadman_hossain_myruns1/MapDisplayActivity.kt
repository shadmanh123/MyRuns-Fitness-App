package com.example.shadman_hossain_myruns1

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
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

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener{
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var locationManager: LocationManager
    private lateinit var map: GoogleMap
    private lateinit var markerOptions: MarkerOptions
    private var startMarker: Marker? = null
    private var currentMarker: Marker? = null
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var polylineList: ArrayList<Polyline>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_display)
        val typeOfActivityCode: Int = intent.getIntExtra("activityCode", -1)
        val typeOfActivityName:String? = intent.getStringExtra("activityName")
        val entryKey = intent.getIntExtra("entryKey", -1)
        Utilities.checkForGPSPermission(this)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            finish()
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            removeMarkersAndPolylines()
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        polylineOptions = PolylineOptions()
        polylineList = ArrayList()
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
        }
        else{
            currentMarker?.remove()
            currentMarker = map.addMarker(MarkerOptions().position(latLng))
        }
        polylineOptions.add(latLng)
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