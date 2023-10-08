package com.example.shadman_hossain_myruns1

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment

class StartFragment:Fragment() {
    private lateinit var activity:String
    private lateinit var view: View
    private lateinit var inputTypeSpinner: Spinner
    private lateinit var activityTypeSpinner:Spinner
    private lateinit var startButton: Button
    private lateinit var intentToManualInputActivity: Intent
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ):
            View?{
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
        var inputType = inputTypeSpinner.selectedItem.toString()
        if (inputType == "Manual"){
            activity = activityTypeSpinner.selectedItem.toString()
            intentToManualInputActivity = Intent(requireContext(), ManualInputActivity::class.java)
            intentToManualInputActivity.putExtra("activity", activity)
            startActivity(intentToManualInputActivity)
        }
        else{
            TODO() //Take them to MapDisplayActivity.kt
        }
    }
}