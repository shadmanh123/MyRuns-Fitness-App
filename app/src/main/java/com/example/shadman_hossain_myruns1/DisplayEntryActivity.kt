package com.example.shadman_hossain_myruns1

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CompletableFuture
import kotlin.math.floor

class DisplayEntryActivity: AppCompatActivity() {
    private lateinit var activityTypeUI:TextView
    private lateinit var dateTimeUI: TextView
    private lateinit var durationUI: TextView
    private lateinit var distanceUI:TextView
    private lateinit var calorieUI: TextView
    private lateinit var heartRateUI: TextView
    private lateinit var commentUI: TextView
    private lateinit var deleteButton: Button
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var repository: ExerciseRepository
    private lateinit var exerciseViewModelFactory: ExerciseViewModelFactory
    private lateinit var exerciseViewModel: ExerciseViewModel
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var formattedDateTime: String
    private lateinit var activity: String
    private lateinit var formattedDuration: String
    private lateinit var formattedDistance: String
    private lateinit var unitPreference: String
    private var entryKey: Long = 0L
    private var activityToDisplay: ExerciseEntry? = null
    private lateinit var unitType: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_entry_activity)
        activityTypeUI = findViewById(R.id.activityTypeValue)
        dateTimeUI = findViewById(R.id.dateTimeValue)
        durationUI = findViewById(R.id.durationValue)
        distanceUI = findViewById(R.id.distanceValue)
        calorieUI = findViewById(R.id.calorieValue)
        heartRateUI = findViewById(R.id.heartRateValue)
        commentUI = findViewById(R.id.commentValue)
        deleteButton = findViewById(R.id.deleteButton)
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        exerciseViewModelFactory = ExerciseViewModelFactory(repository)
        exerciseViewModel = ViewModelProvider(this, exerciseViewModelFactory).get(ExerciseViewModel::class.java)

        entryKey = intent.getLongExtra("entryKey", 0L)

        activityToDisplay = getActivitytoDisplay(entryKey)
        if(entryKey != 0L) {
            val activityResult = checkActivityType(activityToDisplay!!.activityType!!.toInt())
            activityTypeUI.text = activityResult
            val dateTimeFormatted = dateTimeFormatter(activityToDisplay!!.dateTime!!)
            dateTimeUI.text = dateTimeFormatted
            val durationFormatted = durationFormatter(activityToDisplay!!.duration!!)
            durationUI.text = durationFormatted
            val distanceFormatted = distanceFormatted(activityToDisplay!!.distance!!, activityToDisplay!!.distanceUnit!!)
            distanceUI.text = distanceFormatted
            calorieUI.text = activityToDisplay!!.calorie.toString()
            heartRateUI.text = activityToDisplay!!.heartRate.toString()
            commentUI.text = activityToDisplay!!.comment.toString()

        }

        deleteButton.setOnClickListener {
            exerciseViewModel.delete(entryKey)
            finish()
        }
    }

    private fun getActivitytoDisplay(entryKey: Long): ExerciseEntry?{
        val exerciseActivityFuture = CompletableFuture<ExerciseEntry>()
        Thread{
            val threadExerciseActivity = exerciseViewModel.getEntryByID(entryKey) as ExerciseEntry
            exerciseActivityFuture.complete(threadExerciseActivity)
        }.start()
        return exerciseActivityFuture.get()
    }

    private fun durationFormatter(duration: Double): String {
        val durationValue = duration
        if(durationValue > 60){
            val hours = floor(durationValue /60)
            val minutes = durationValue - 60*hours
            formattedDuration = hours.toInt().toString()+" hours : "+minutes.toInt().toString()+" mins"
        }
        else{
            formattedDuration = durationValue.toString() + " mins"
        }
        return formattedDuration
    }

    private fun getUnitPreference(): String{
        unitType = this.getSharedPreferences("Unit", AppCompatActivity.MODE_PRIVATE)
        var type = unitType.getString("unitType", "km")
        return type!!
    }

    private fun distanceFormatted(distance: Double, unitTypeSaved: String): String {
        val distanceValue = distance.toString()
        unitPreference = getUnitPreference()
        if(unitPreference == unitTypeSaved){
            formattedDistance = distanceValue+ " " + unitPreference
        }
        else{
            formattedDistance = convertDistanceValue(distanceValue, unitTypeSaved)+" "+unitPreference
        }


        return formattedDistance
    }

    private fun convertDistanceValue(distanceValue: String, unitTypeSaved: String):String {
        var distanceValueDouble = distanceValue.toDouble()
        if (unitPreference == "km" && unitTypeSaved == "miles"){
            distanceValueDouble = (distanceValueDouble*1.60934)
            return distanceValueDouble.toString()
        }
        else{
            distanceValueDouble = (distanceValueDouble*0.621371)
            return distanceValueDouble.toString()
        }

    }

    private fun dateTimeFormatter(dateTime: Long): String {
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formattedDateTime = dateFormat.format(Date(dateTime))
        return formattedDateTime
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