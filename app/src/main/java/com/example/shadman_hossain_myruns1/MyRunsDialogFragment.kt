package com.example.shadman_hossain_myruns1

import android.app.AlertDialog.Builder
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class MyRunsDialogFragment:DialogFragment() {
    private lateinit var title: TextView
    private lateinit var userInput: EditText
    private lateinit var view: View
    private lateinit var calendar: Calendar
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker

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
        val dialogType = bundle!!.getString("dialogType")
        val builder = Builder(requireActivity())
        view = requireActivity().layoutInflater.inflate(R.layout.simple_dialog,null)
        builder.setView(view)
        title = view.findViewById(R.id.dialogTitle)
        userInput = view.findViewById(R.id.userInput)
        calendar = Calendar.getInstance()
        checkDialogType(dialogType, builder)
        return builder.create()
    }

    private fun checkDialogType(dialogType: String?, builder: Builder) {
        if (dialogType == "Date"){
            createCalenderWidget(builder)
        }
        else if(dialogType == "Time"){
            createTimeWidget(builder)
        }
        else if (dialogType == "Duration") {
            title.text = "Duration"
            var duration:Long = userInput.toString().toLong()
        }
        else if (dialogType == "Distance") {
            title.text = "Distance"
            var distance :Long = userInput.toString().toLong()
        }
        else if (dialogType == "Calories") {
            title.text = "Calories"
            var calories:Long = userInput.toString().toLong()
        }
        else if (dialogType == "Heart Rate") {
            title.text = "Heart Rate"
            var heartRate:Long = userInput.toString().toLong()
        }
        else if (dialogType == "Comment") {
            title.text = "Comment"
            userInput.hint = "Note your thoughts about today's activity here?"
            userInput.inputType = InputType.TYPE_CLASS_TEXT
            var comment:String = userInput.toString()
        }
    }

    private fun createTimeWidget(builder: Builder) {
        timePicker = TimePicker(requireContext())
        timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
        timePicker.minute = calendar.get(Calendar.MINUTE)
        builder.setView(timePicker)
        builder.setPositiveButton("OK") { _, _ ->
            val selectedTime: Long = "${timePicker.hour}:${timePicker.minute}".toLong()
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
            val selectedDate:Long = "${datePicker.year}/${datePicker.month + 1}/${datePicker.dayOfMonth}".toLong()
        }
        builder.setNegativeButton("Cancel") { _, _ ->
            dismiss()
        }
    }
}