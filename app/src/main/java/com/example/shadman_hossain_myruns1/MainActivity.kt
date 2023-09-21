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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userName = findViewById<EditText>(R.id.userName)
        val saveButton = findViewById<Button>(R.id.saveButton)
        saveButton.setOnClickListener {
            saveProfile()
        }
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    fun saveProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        val editor = savedProfiles.edit()
        val userEmail = findViewById<EditText>(R.id.userEmail).text.toString()
        val userPhoneNumber = findViewById<EditText>(R.id.userPhoneNumber).text.toString()
        val userMajor = findViewById<EditText>(R.id.userMajor).text.toString()
        val userGender = findViewById<RadioGroup>(R.id.genderButtonRadioGroup)
        var selectedGender: String?
        userGender.setOnCheckedChangeListener { group, checkedId ->
            val selectedGenderButton = findViewById<RadioButton>(checkedId)
            selectedGender = selectedGenderButton.text.toString()
            editor.apply(){
                putString("UserName", userName.text.toString())
                putString("UserEmail", userEmail)
                putString("UserPhoneNumber", userPhoneNumber)
                putString("UserMajor", userMajor)
                putString("UserGender", selectedGender)
                apply()
            }
        }
        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
        }

    fun loadProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        val editor = savedProfiles.edit()
        userName = findViewById<EditText>(R.id.userName)
        val userEmail = findViewById<EditText>(R.id.userEmail)
        val userPhoneNumber = findViewById<EditText>(R.id.userPhoneNumber)
        val userMajor = findViewById<EditText>(R.id.userMajor)
        val userGender = findViewById<RadioGroup>(R.id.genderButtonRadioGroup)
        if (savedProfiles != null) {
            userName.setText(savedProfiles.getString("UserName", null))
            userEmail.setText(savedProfiles.getString("UserEmail", null))
            userPhoneNumber.setText(savedProfiles.getString("UserPhoneNumber", null))
            userMajor.setText(savedProfiles.getString("UserMajor", null))

        }
    }
    }

