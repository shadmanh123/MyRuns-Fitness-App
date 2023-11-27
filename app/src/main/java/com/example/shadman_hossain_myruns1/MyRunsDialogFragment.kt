package com.example.shadman_hossain_myruns1

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class MyRunsDialogFragment:DialogFragment() {
    private lateinit var dialogType: String
    private lateinit var title: TextView
    private lateinit var userInput: EditText
    private lateinit var view: View
    private lateinit var calendar: Calendar
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var selectedDate:Calendar
    private var date: Long? = null
    private var time: Long? = null
    private var data: String? = null

    companion object{
        fun newInstance(dialogType: String):MyRunsDialogFragment{
            val fragment = MyRunsDialogFragment()
            val arguments = Bundle()
            arguments.putString("dialogType", dialogType)
            fragment.arguments = arguments
            return fragment
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bundle = arguments
        dialogType = bundle!!.getString("dialogType")!!
        val builder = Builder(requireActivity())
        view = requireActivity().layoutInflater.inflate(R.layout.simple_dialog,null)
        builder.setView(view)
        title = view.findViewById(R.id.dialogTitle)
        userInput = view.findViewById(R.id.userInput)
        calendar = Calendar.getInstance()
        checkDialogType(dialogType, builder)
        saveButton = view.findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            if(userInput.text.toString().isNotEmpty()) {
                if(dialogType == "Distance"){ //Checking if distance because distance data format is different than rest
                    var userInputResponse = userInput.text.toString() + " " + returnUnitPreference()
                    data = userInputResponse
                    dataSentToManualInputActivity()
                }
                else {
                    data = userInput.text.toString()
                    dataSentToManualInputActivity()
                }
            }
            else{
                Toast.makeText(context, "Input information", Toast.LENGTH_SHORT).show()
            }
        }
        cancelButton = view.findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            dismiss()
        }
        return builder.create()
    }

    private fun checkDialogType(dialogType: String?, builder: Builder) {
        if (dialogType == "Date"){
            createCalenderWidget(builder).toString()
        }
        else if(dialogType == "Time"){
            createTimeWidget(builder).toString()
        }
        else if (dialogType == "Duration") {
            title.text = "Duration"
            userInput.hint = "Input duration in minutes"
        }
        else if (dialogType == "Distance") {
            title.text = "Distance"
            userInput.hint = "Input distance information "
        }
        else if (dialogType == "Calories") {
            title.text = "Calories"
        }
        else if (dialogType == "Heart Rate") {
            title.text = "Heart Rate"
        }
        else if (dialogType == "Comment") {
            title.text = "Comment"
            userInput.hint = "Note your thoughts about today's activity here?"
            userInput.inputType = InputType.TYPE_CLASS_TEXT
        }
    }

    private fun createTimeWidget(builder: Builder) {
        timePicker = TimePicker(requireContext())
        timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
        timePicker.minute = calendar.get(Calendar.MINUTE)
        builder.setView(timePicker)
        builder.setPositiveButton("OK") { _, _ ->
            time = ((timePicker.hour*60*60*1000) + (timePicker.minute*60*1000)).toLong()
            data = time.toString()
            dataSentToManualInputActivity()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            dismiss()
        }
    }

    private fun createCalenderWidget(builder: Builder) {
        datePicker = DatePicker(requireContext())
        datePicker.init(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH), null
        )
        builder.setView(datePicker)
        builder.setPositiveButton("OK") { _, _ ->
            selectedDate = Calendar.getInstance()
            selectedDate.set(datePicker.year, datePicker.month, datePicker.dayOfMonth)
            date = selectedDate.timeInMillis
            data = date.toString()
            dataSentToManualInputActivity()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            dismiss()
        }
    }

    private fun returnUnitPreference(): String{
        val unitType = this.requireContext().getSharedPreferences("Unit", AppCompatActivity.MODE_PRIVATE)
        var type = unitType.getString("unitType", "km")
        if (type == "miles"){
            return "mi"
        }
        else{
            return "km"
        }
    }

    private fun dataSentToManualInputActivity() {
        if (requireActivity() is ManualInputActivity) {
            val activity = requireActivity() as ManualInputActivity
            activity.onDataSaved(data!!, dialogType)
            dismiss()
        }
    }
}