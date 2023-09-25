package com.example.shadman_hossain_myruns1

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.net.URI

class MainActivity : AppCompatActivity() {
    private lateinit var userName:EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhoneNumber:EditText
    private lateinit var userClass: EditText
    private lateinit var userMajor:EditText
    private lateinit var userGender:RadioGroup
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var profilePhoto: ImageView
    private lateinit var photoButton: Button
    private lateinit var tempImageFile: File
    private lateinit var tempImageUri:Uri
    private lateinit var bitmap: Bitmap
    private val tempImgFileName = "profile_photos.jpg"
    private lateinit var takePhotoIntent: ActivityResultLauncher<Intent>
    private lateinit var viewProfilePhoto: MyViewModel
    private var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    private var allValuesEntered = false
    private var newProfilePhotoCaptured = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        Utilities.checkForPermission(this)
        profilePhoto = findViewById(R.id.ProfilePhoto)
        setProfilePhoto()
        photoButton = findViewById(R.id.changeProfilePictureButton)
        viewProfilePhoto = ViewModelProvider(this).get(MyViewModel::class.java)
        viewProfilePhoto.photoOfUser.observe(this, Observer{it ->
            profilePhoto.setImageBitmap(it)
        })
        takePhotoIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: androidx.activity.result.ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                val bitmap = Utilities.getBitMap(this, tempImageUri)
                viewProfilePhoto.photoOfUser.value = bitmap
            }
        }
        photoButton.setOnClickListener {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
            takePhoto(intent)
        }
        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userPhoneNumber = findViewById(R.id.userPhoneNumber)
        userClass = findViewById(R.id.userClass)
        userMajor = findViewById(R.id.userMajor)
        userGender = findViewById(R.id.genderButtonRadioGroup)
        loadProfile()
        saveButton = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            if(allValuesEntered==false){
                checkValidInputs()
            }
            else{
                saveProfile()
            }
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun takePhoto(intent: Intent){
        takePhotoIntent.launch(intent)
        newProfilePhotoCaptured = true
        setProfilePhoto()
    }
    private fun setProfilePhoto() {
        tempImageFile = File(getExternalFilesDir(null), tempImgFileName)
        if (tempImageFile.length() != 0.toLong()) {
            tempImageUri = FileProvider.getUriForFile(this, "com.MyRuns", tempImageFile)
            bitmap = Utilities.getBitMap(this, tempImageUri)
            profilePhoto.setImageBitmap(bitmap)
        } else {
            tempImageFile.createNewFile()
            tempImageUri = FileProvider.getUriForFile(this, "com.MyRuns", tempImageFile)
        }
    }

    private fun checkValidInputs(){
        if(userName.text.toString().isEmpty()){
            Toast.makeText(this,"Input a username", Toast.LENGTH_SHORT).show()
        }
        else if(userEmail.text.toString().isEmpty()){
            Toast.makeText(this,"Input an email", Toast.LENGTH_SHORT).show()
        }
        else if(userPhoneNumber.text.toString().isEmpty()){
            Toast.makeText(this,"Input a phone number", Toast.LENGTH_SHORT).show()
        }
        else if(userGender.checkedRadioButtonId == -1){
            Toast.makeText(this,"Select a gender", Toast.LENGTH_SHORT).show()
        }
        else if(userClass.text.toString().isEmpty()){
            Toast.makeText(this,"Input a class year", Toast.LENGTH_SHORT).show()
        }
        else if(userMajor.text.toString().isEmpty()){
            Toast.makeText(this,"Input a major", Toast.LENGTH_SHORT).show()
        }
        else{
            allValuesEntered = true
        }
    }
    private fun saveProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        val editor = savedProfiles.edit()
        editor.apply {
            putString("UserName", userName.text.toString())
            putString("UserEmail", userEmail.text.toString())
            putString("UserPhoneNumber", userPhoneNumber.text.toString())
            putInt("UserClass", userClass.text.toString().toInt())
            putString("UserMajor", userMajor.text.toString())
            putInt("UserGender", userGender.checkedRadioButtonId)
            if(newProfilePhotoCaptured == true){
                editor.putString("ProfilePhoto", tempImageUri.toString())
            }
            apply()
        }

        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        val tempImageUriString:String? = savedProfiles.getString("Profile Photo", null)
        val userClassInt:Int = savedProfiles.getInt("UserClass",-1)
        if (savedProfiles != null) {
            userName.setText(savedProfiles.getString("UserName", null))
            userEmail.setText(savedProfiles.getString("UserEmail", null))
            userPhoneNumber.setText(savedProfiles.getString("UserPhoneNumber", null))
            userClass.setText(userClassInt.toString())
            userMajor.setText(savedProfiles.getString("UserMajor", null))
            userGender.check(savedProfiles.getInt("UserGender", -1))
        }
        if(tempImageUriString != null){
            bitmap = Utilities.getBitMap(this, Uri.parse(tempImageUriString))
            profilePhoto.setImageBitmap(bitmap)
        }
    }

}