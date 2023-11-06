package com.example.shadman_hossain_myruns1

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar

class ManualInputActivity:AppCompatActivity() {
    private val inputTitles = arrayOf(
        "Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment"
    )
    private lateinit var listView:ListView
    private var typeOfActivityCode: Int = -1
    private var typeOfActivityName: String? = null
    private var inputTypeCode: Int = -1
    private lateinit var dialogFragment: MyRunsDialogFragment
    private lateinit var arguments: Bundle
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private var date: Long? = null
    private var time: Long? = null
    private var dateTime: Long? = null
    private var duration: Double? = null
    private var distance: Double? = null
    private var calories: Double? = null
    private var heartRate: Double? = null
    private var comment: String? = null
    private var flag = false
    private lateinit var dateCalendar: Calendar
    private lateinit var timeCalendar: Calendar
    private lateinit var dateTimeCalendar: Calendar
    private lateinit var database: ExerciseDatabase
    private lateinit var databaseDao: ExerciseDatabaseDao
    private lateinit var repository: ExerciseRepository
    private lateinit var exerciseViewModelFactory: ExerciseViewModelFactory
    private lateinit var exerciseViewModel: ExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_input_interface)
        typeOfActivityCode = intent.getIntExtra("activityCode", -1)
        typeOfActivityName = intent.getStringExtra("activityName")
        inputTypeCode = intent.getIntExtra("inputTypeCode",-1 )
        database = ExerciseDatabase.getInstance(this)
        databaseDao = database.exerciseDatabaseDao
        repository = ExerciseRepository(databaseDao)
        exerciseViewModelFactory = ExerciseViewModelFactory(repository)
        exerciseViewModel = ViewModelProvider(this, exerciseViewModelFactory).get(ExerciseViewModel::class.java)
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            if (checkAllValuesSaved() == true){
                saveToExerciseDatabase()
                finish()
            }
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }


        listView = findViewById(R.id.listView)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, inputTitles)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener(){ parent: AdapterView<*>, view: View, position: Int, id: Long ->
            val dialogType = inputTitles[position]
            val dialogFragment = MyRunsDialogFragment.newInstance(dialogType)
            dialogFragment.show(supportFragmentManager, "MyRunsDialog")
//            dialogFragment.setDataSavedListener(this)
        }

    }

    private fun saveToExerciseDatabase() {
        getDateTime()
        var exerciseEntry = ExerciseEntry(
            inputType = inputTypeCode, activityType = typeOfActivityCode, dateTime = dateTime,
            duration = duration, distance = distance, calorie = calories, heartRate = heartRate,
            comment = comment
        )
        exerciseViewModel.insert(exerciseEntry)
    }

    private fun getDateTime() {
        dateCalendar = Calendar.getInstance()
        dateCalendar.timeInMillis = date!!
        timeCalendar = Calendar.getInstance()
        timeCalendar.timeInMillis = time!!
        val year = dateCalendar.get(Calendar.YEAR)
        val month = dateCalendar.get(Calendar.MONTH)
        val day = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val hour = timeCalendar.get(Calendar.HOUR_OF_DAY)
        val minute = timeCalendar.get(Calendar.MINUTE)
        val seconds = timeCalendar.get(Calendar.SECOND)
        dateTimeCalendar = Calendar.getInstance()
        dateTimeCalendar.set(year, month, day, hour, minute, seconds)
        dateTime = dateTimeCalendar.timeInMillis
    }

    fun onDataSaved(data: String, dialogType: String) {
        if (dialogType == "Date"){
            date = data.toLong()
        }
        else if (dialogType == "Time"){
            time = data.toLong()
        }
        else if (dialogType == "Duration"){
            duration = data.toDouble()
        }
        else if (dialogType == "Distance"){
            distance = data.toDouble()
        }
        else if (dialogType == "Calories"){
            calories = data.toDouble()
        }
        else if (dialogType == "Heart Rate"){
            heartRate = data.toDouble()
        }
        else if (dialogType == "Comment"){
            comment = data
        }
    }
    private fun checkAllValuesSaved(): Boolean {

        if(date == null){
            Toast.makeText(this, "Select Date", Toast.LENGTH_SHORT).show()
        }
        else if (time == null){
            Toast.makeText(this,"Select Time", Toast.LENGTH_SHORT).show()
        }

        else if (duration == null)  {
            Toast.makeText(this, "Input Duration", Toast.LENGTH_SHORT).show()
        }

        else if (distance == null){
            Toast.makeText(this, "Input Distance", Toast.LENGTH_SHORT).show()
        }

        else if (calories == null){
            Toast.makeText(this, "Input Calories", Toast.LENGTH_SHORT).show()
        }

        else if (heartRate == null){
            Toast.makeText(this, "Input Heart Rate", Toast.LENGTH_SHORT).show()
        }

        else if (comment == null){
            Toast.makeText(this, "Input Comment", Toast.LENGTH_SHORT).show()
        }
        else{
            flag = true
        }
        return flag
    }
}
