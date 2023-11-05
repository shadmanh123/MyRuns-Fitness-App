package com.example.shadman_hossain_myruns1

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class ManualInputActivity:AppCompatActivity() {
    private val inputTitles = arrayOf(
        "Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment"
    )
    private lateinit var listView:ListView
    private lateinit var dialogFragment: MyRunsDialogFragment
    private lateinit var arguments: Bundle
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_input_interface)
        val typeOfActivityCode: Int = intent.getIntExtra("activityCode", -1)
        val typeOfActivityName:String? = intent.getStringExtra("activityName")
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            finish()
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
        }

    }
}
