package com.example.shadman_hossain_myruns1

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
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
        var durationValue = entryList.get(position).duration.toString()
        if(durationValue.toDouble() > 60){
            var hours = floor(durationValue.toDouble()/60)
            var minutes = durationValue.toDouble() - 60*hours
            formattedDuration = hours.toInt().toString()+" hours : "+minutes.toInt().toString()+" mins"
        }
        else{
            formattedDuration = durationValue.toString() + " mins"
        }
        duration.text = formattedDuration
    }

    private fun distanceFormat(position: Int) {
        var distanceValue = entryList.get(position).distance.toString()
        var formattedDistance = distanceValue+unitPreference
        distance.text = formattedDistance
    }


//    fun getUnitPreference(key: Int){
//        if(key == 1){
//            unitPreference = "km"
//        }
//        else{
//            unitPreference = "miles"
//        }
//    }


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
        var activityTypeCode = entryList.get(position).activityType
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