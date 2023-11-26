package com.example.shadman_hossain_myruns1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment

class StartFragment:Fragment() {
    private lateinit var activity:String
    private lateinit var view: View
    private lateinit var inputTypeSpinner: Spinner
    private lateinit var activityTypeSpinner:Spinner
    private lateinit var inputType:String
    private lateinit var startButton: Button
    private lateinit var intentToManualInputActivity: Intent
    private lateinit var intentToMapDisplayActivity: Intent
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View {
        view = inflater.inflate(R.layout.start_fragment, container,false)
        activityTypeSpinner = view.findViewById<Spinner>(R.id.activityTypeSpinner)
        inputTypeSpinner = view.findViewById(R.id.inputTypeSpinner)
        startButton = view.findViewById(R.id.startButton)
        startButton.setOnClickListener {
            checkInputType()
        }

    return view
    }
    private fun checkInputType(){
        inputType = inputTypeSpinner.selectedItem.toString()
        if (inputType == "Manual"){
            activity = activityTypeSpinner.selectedItem.toString()
            val inputTypeCode = 1
            val activityCode = getActivityTypeCode(activity)
            if(activityCode != -1) {
                intentToManualInputActivity =
                    Intent(requireContext(), ManualInputActivity::class.java)
                intentToManualInputActivity.putExtra("activityCode", activityCode)
                intentToManualInputActivity.putExtra("activityName", activity)
                intentToManualInputActivity.putExtra("inputTypeCode", inputTypeCode)
                startActivity(intentToManualInputActivity)
            }
        }
        else if (inputType == "GPS") {
            activity = activityTypeSpinner.selectedItem.toString()
            var inputTypeValue: Int = 2
            val activityCode = getActivityTypeCode(activity)
            if(activityCode != -1){
                intentToMapDisplayActivity = Intent(requireContext(), MapDisplayActivity::class.java)
                intentToMapDisplayActivity.putExtra("activityCode", activityCode)
                intentToMapDisplayActivity.putExtra("activityName", activity)
                intentToMapDisplayActivity.putExtra("inputTypeValue", inputTypeValue)
                startActivity(intentToMapDisplayActivity)
            }
        }

        else{
            var inputTypeValue: Int = 3
            intentToMapDisplayActivity = Intent(requireContext(),MapDisplayActivity::class.java)
            intentToMapDisplayActivity.putExtra("inputTypeCode", inputTypeValue)
            startActivity(intentToMapDisplayActivity)
        }
    }
    private fun getActivityTypeCode(activityType:String):Int{
        var code = -1
        if(activityType == "Running"){
            code = 0
        }
        else if(activityType == "Walking"){
            code = 1
        }
        else if(activityType == "Standing"){
            code = 2
        }
        else if(activityType == "Cycling"){
            code = 3
        }
        else if(activityType == "Hiking"){
            code = 4
        }
        else if(activityType == "Downhill Skiing"){
            code = 5
        }
        else if(activityType == "Cross-Country Skiing"){
            code = 6
        }
        else if(activityType == "Snowboarding"){
            code = 7
        }
        else if(activityType == "Skating"){
            code = 8
        }
        else if(activityType == "Swimming"){
            code = 9
        }
        else if(activityType == "Mountain Biking"){
            code = 10
        }
        else if(activityType == "Wheelchair"){
            code = 11
        }
        else if(activityType == "Elliptical"){
            code = 12
        }
        else if(activityType == "Other"){
            code = 13
        }
        return code
    }
}