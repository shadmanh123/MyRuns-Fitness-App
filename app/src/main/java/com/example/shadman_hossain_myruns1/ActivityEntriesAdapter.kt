package com.example.shadman_hossain_myruns1

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.lang.Math.floor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActivityEntriesAdapter(private val context: Context, private var entryList: List<ExerciseEntry>):
    BaseAdapter() {
    private lateinit var view: View
    private lateinit var activityType: TextView
    private lateinit var activityTime: TextView
    private lateinit var distance: TextView
    private lateinit var duration: TextView
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var formattedDateTime: String
    private lateinit var activity: String
    private lateinit var unitPreference:String
    private lateinit var formattedDuration: String
    private lateinit var durationValue: String
    private lateinit var distanceValue: String
    private lateinit var formattedDistance: String
    private lateinit var unitType: SharedPreferences
    private lateinit var unitTypeSaved: String

    //    private lateinit var unitPreference: Int
    override fun getCount(): Int {
        return entryList.size
    }

    override fun getItem(p0: Int): ExerciseEntry {
        return entryList.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        view = View.inflate(context, R.layout.activity_entry_adapter, null)
        activityType = view.findViewById(R.id.activityType)
        activityTime = view.findViewById(R.id.activityTime)
        distance = view.findViewById(R.id.distance)
        duration = view.findViewById(R.id.duration)
        checkActivityType(position)
        dateTimeFormat(position)
        distanceFormat(position)
        durationFormat(position)
        return view
    }

    private fun durationFormat(position: Int) {
        durationValue = entryList.get(position).duration.toString()
        Log.d("Activity Entries Adapter", "Duration Value $durationValue")
        if(durationValue.toDouble() > 60){
            val hours = floor(durationValue.toDouble()/60)
            val minutes = durationValue.toDouble() - 60*hours
            formattedDuration = hours.toInt().toString()+" hours : "+minutes.toInt().toString()+" mins"
        }
        else{
            formattedDuration = durationValue + " mins"
        }
        duration.text = formattedDuration
    }

    private fun distanceFormat(position: Int) {
        distanceValue = entryList.get(position).distance.toString()
        unitPreference = getUnitPreference()
        unitTypeSaved = determineUnitTypeSaved(position)
        if (unitTypeSaved == unitPreference) {
            formattedDistance = distanceValue + " "+ unitPreference
        }
        else{
            formattedDistance = convertDistanceValue(distanceValue) + " " + unitPreference
        }
        distance.text = formattedDistance
    }

    private fun convertDistanceValue(distanceValue: String):String {
        var distanceValueDouble = distanceValue.toDouble()
        if (unitPreference == "km" && unitTypeSaved == "mi"){
            distanceValueDouble = (distanceValueDouble*1.60934)
            return distanceValueDouble.toString()
        }
        else{
            distanceValueDouble = (distanceValueDouble*0.621371)
            return distanceValueDouble.toString()
        }

    }

    private fun determineUnitTypeSaved(position: Int):String {
        val unitTypeStored = entryList.get(position).distanceUnit!!
        if (unitTypeStored == "mi"){
            return "mi"
        }
        else{
            return "km"
        }
    }


    private fun getUnitPreference(): String{
        unitType = context.getSharedPreferences("Unit", AppCompatActivity.MODE_PRIVATE)
        val type = unitType.getString("unitType", "km")
        if (type == "miles"){
            return "mi"
        }
        else{
            return "km"
        }
    }

    private fun dateTimeFormat(position: Int) {
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formattedDateTime = dateFormat.format(Date(entryList.get(position).dateTime!!))
        activityTime.text = formattedDateTime
    }

    fun replace(newEntryList: List<ExerciseEntry>){
        entryList = newEntryList
    }
    fun checkActivityType(position: Int) {
        val activityTypeCode = entryList.get(position).activityType
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
        activityType.text = activity
    }


}