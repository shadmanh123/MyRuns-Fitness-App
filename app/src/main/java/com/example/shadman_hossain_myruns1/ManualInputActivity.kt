package com.example.shadman_hossain_myruns1

import android.app.AlertDialog.Builder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ManualInputActivity:AppCompatActivity() {
    private val inputTitles = arrayOf(
        "Date", "Time", "Duration", "Distance", "Calories", "Heart Rate", "Comment"
    )
    private lateinit var listView:ListView
    private lateinit var dialogFragment: MyRunsDialogFragment
    private lateinit var arguments: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manual_input_interface)
        val typeOfActivity: String? = intent.getStringExtra("activity")
        Toast.makeText(this, typeOfActivity, Toast.LENGTH_SHORT).show()

        //ListView Inspired by lecture
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
