package com.example.shadman_hossain_myruns1

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MapDisplayActivity:AppCompatActivity() {
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_display)
        val typeOfActivityCode: Int = intent.getIntExtra("activityCode", -1)
        val typeOfActivityName:String? = intent.getStringExtra("activityName")
//        Toast.makeText(this, typeOfActivityName.toString(), Toast.LENGTH_SHORT).show()
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            finish()
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }

    }
}