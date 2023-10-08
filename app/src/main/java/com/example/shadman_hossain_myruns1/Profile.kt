package com.example.shadman_hossain_myruns1

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.activity.result.ActivityResultLauncher
import java.io.File
import android.app.Activity
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.nio.file.Files
import java.nio.file.StandardCopyOption
class Profile:AppCompatActivity() {
    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPhoneNumber: EditText
    private lateinit var userClass: EditText
    private lateinit var userMajor: EditText
    private lateinit var userGender: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var profilePhoto: ImageView
    private lateinit var photoButton: Button
    private lateinit var tempImageFile: File
    private lateinit var savedImageFile: File
    private lateinit var tempImageUri: Uri
    private lateinit var savedImageUri: Uri
    private lateinit var bitmap: Bitmap
    private val tempImgFileName = "profile_photos.jpg"
    private val savedImageFileName = "saved_profile_photo.jpg"
    private lateinit var takePhotoIntent: ActivityResultLauncher<Intent>
    private lateinit var viewProfilePhoto: MyViewModel
    private var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    private var allValuesEntered = false
    private var firstTimeLoad = false
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        Utilities.checkForPermission(this)

        profilePhoto = findViewById(R.id.ProfilePhoto)

        photoButton = findViewById(R.id.changeProfilePictureButton)


        userName = findViewById(R.id.userName)
        userEmail = findViewById(R.id.userEmail)
        userPhoneNumber = findViewById(R.id.userPhoneNumber)
        userClass = findViewById(R.id.userClass)
        userMajor = findViewById(R.id.userMajor)
        userGender = findViewById(R.id.genderButtonRadioGroup)
        saveButton = findViewById(R.id.saveButton)
        loadProfile()
        setProfilePhoto()
        viewProfilePhoto = ViewModelProvider(this).get(MyViewModel::class.java)
        viewProfilePhoto.photoOfUser.observe(this) {
            profilePhoto.setImageBitmap(it)
        }
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
        saveButton.setOnClickListener {
            if(!allValuesEntered){
                checkValidInputs()
            }
            else{
                saveProfile()
            }
        }
        cancelButton = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            tempImageFile.delete()
            finish()
        }
    }
    private fun takePhoto(intent: Intent){
        takePhotoIntent.launch(intent)
        setProfilePhoto()
    }
    private fun setProfilePhoto() {
        tempImageFile = File(getExternalFilesDir(null), tempImgFileName)
//        savedImageFile = File(getExternalFilesDir(null),savedImageFileName)
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
        else if(profilePhoto.drawable == null){
            Toast.makeText(this,"Take a photo", Toast.LENGTH_SHORT).show()
        }
        else{
            allValuesEntered = true
        }
    }
    private fun saveProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        val editor = savedProfiles.edit()
        savedImageFile = File(getExternalFilesDir(null),savedImageFileName)

        if(tempImageFile.exists()) {
            Files.copy(
                tempImageFile.inputStream(),
                savedImageFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING
            )

        }
        savedImageUri = FileProvider.getUriForFile(this, "com.MyRuns", savedImageFile)
        firstTimeLoad = true
        editor.apply {
            putString("UserName", userName.text.toString())
            putString("UserEmail", userEmail.text.toString())
            putString("UserPhoneNumber", userPhoneNumber.text.toString())
            putInt("UserClass", userClass.text.toString().toInt())
            putString("UserMajor", userMajor.text.toString())
            putInt("UserGender", userGender.checkedRadioButtonId)
            putString("ProfilePhoto", savedImageUri.toString())
            putBoolean("FirstTimeLoad", firstTimeLoad)
            apply()
        }
        tempImageFile.delete()

        Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
    }

    private fun loadProfile(){
        val savedProfiles = getSharedPreferences("Profiles", MODE_PRIVATE)
        val savedImageUriString:String? = savedProfiles.getString("ProfilePhoto", null)
        val userClassInt:Int = savedProfiles.getInt("UserClass",-1)
        val firstTimeLoad = savedProfiles.getBoolean("FirstTimeLoad", false)
        if (savedProfiles != null) {
            userName.setText(savedProfiles.getString("UserName", null))
            userEmail.setText(savedProfiles.getString("UserEmail", null))
            userPhoneNumber.setText(savedProfiles.getString("UserPhoneNumber", null))
            userClass.setText(userClassInt.toString())
            userMajor.setText(savedProfiles.getString("UserMajor", null))
            userGender.check(savedProfiles.getInt("UserGender", -1))
        }
        if(firstTimeLoad){
            bitmap = Utilities.getBitMap(this, Uri.parse(savedImageUriString))
            profilePhoto.setImageBitmap(bitmap)
        }
    }

    override fun onStop() {
        tempImageFile.delete()
        super.onStop()
    }

}