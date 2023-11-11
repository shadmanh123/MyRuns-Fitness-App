package com.example.shadman_hossain_myruns1

import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.floor

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
    private var unitPreference:String = "km"
    private lateinit var formattedDuration: String
    private lateinit var durationValue: String
    private lateinit var distanceValue: String
    private lateinit var formattedDistance: String
    private lateinit var distanceValueList: List<String>
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
        getUnitPreference()
        unitTypeSaved = determineUnitTypeSaved(distanceValue)
        if (unitTypeSaved == unitPreference) {
            formattedDistance = distanceValue + unitPreference
        }
        else{
            formattedDistance = convertDistanceValue(distanceValue)
        }
        distance.text = formattedDistance
    }

    private fun convertDistanceValue(distanceValue: String):String {
        var distanceValueInt = distanceValueList[0].toInt()
        if (unitPreference == "km"){
            distanceValueInt = (distanceValueInt*1.60934).toInt()
            return distanceValueInt.toString()
        }
        else{
            distanceValueInt = (distanceValueInt*0.621371).toInt()
            return distanceValueInt.toString()
        }

    }

    private fun determineUnitTypeSaved(distanceValue: String):String {
        distanceValueList = distanceValue.split(" ")
        var unitTypeStored = distanceValueList[1]
        if (unitTypeStored == "miles"){
            return "mi"
        }
        else{
            return "km"
        }
    }


    fun getUnitPreference(){
        unitType = context.getSharedPreferences("Unit", AppCompatActivity.MODE_PRIVATE)
        var type = unitType.getString("unitType", null)
        if (type != null){
            unitPreference = type
        }
    }


//    fun setUnitPreference(unit: String){
//        unitPreference = unit
//        notifyDataSetChanged()
//    }

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