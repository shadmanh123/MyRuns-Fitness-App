package com.example.shadman_hossain_myruns1

import android.bluetooth.BluetoothClass.Device.Major
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.*
import java.util.jar.Attributes.Name

class MainActivity : AppCompatActivity() {
    private lateinit var userName:EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhoneNumber:EditText
    private lateinit var userMajor:EditText
    private lateinit var userGender:RadioGroup
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userPhoneNumber = findViewById(R.id.userPhoneNumber)
        userMajor = findViewById(R.id.userMajor)
        userGender = findViewById(R.id.genderButtonRadioGroup)
        loadProfile()
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            saveProfile()
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun saveProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        val editor = savedProfiles.edit()
        editor.apply {
            putString("UserName", userName.text.toString())
            putString("UserEmail", userEmail.text.toString())
            putString("UserPhoneNumber", userPhoneNumber.text.toString())
            putString("UserMajor", userMajor.text.toString())
            putInt("UserGender", userGender.checkedRadioButtonId)
            apply()
        }
        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
        }

    private fun loadProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        if (savedProfiles != null) {
            userName.setText(savedProfiles.getString("UserName", null))
            userEmail.setText(savedProfiles.getString("UserEmail", null))
            userPhoneNumber.setText(savedProfiles.getString("UserPhoneNumber", null))
            userMajor.setText(savedProfiles.getString("UserMajor", null))
            userGender.check(savedProfiles.getInt("UserGender", -1))
        }
    }
    }

