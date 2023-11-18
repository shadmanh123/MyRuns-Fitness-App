package com.example.shadman_hossain_myruns1

import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener{
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var locationManager: LocationManager
    private var isMapCentered = false
    private lateinit var map: GoogleMap
    private lateinit var markerOptions: MarkerOptions
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
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        map.mapType = GoogleMap.MAP_TYPE_NORMAL
        map.setOnMapClickListener(this)
        map.setOnMapLongClickListener(this)
        polylineOptions = PolylineOptions()
        polylineList = ArrayList()
        markerOptions = MarkerOptions()
    }

    private fun initializeLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null)
                onLocationChanged(location)

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        } catch (e: SecurityException) {
        }
    }

    override fun onLocationChanged(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        val latLng = LatLng(latitude,longitude)
        if(!isMapCentered){
            val updateCamera = CameraUpdateFactory.newLatLng(latLng)
            map.animateCamera(updateCamera)
            markerOptions.position(latLng)
            map.addMarker(markerOptions)
            polylineOptions.add(latLng)
            isMapCentered = true
        }
    }

    override fun onMapClick(latLng: LatLng) {
        for(i in polylineList.indices){
            polylineList[i].remove()
        }
        polylineOptions.points.clear()
    }

    override fun onMapLongClick(latLng: LatLng) {
        markerOptions.position(latLng!!)
        map.addMarker(markerOptions)
        polylineOptions.add(latLng)
        polylineList.add(map.addPolyline(polylineOptions))
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
