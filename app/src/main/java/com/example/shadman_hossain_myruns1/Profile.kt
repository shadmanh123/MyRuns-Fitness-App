package com.example.shadman_hossain_myruns1

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File
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
    private lateinit var dialogView: View
    private val optionTitles = arrayOf(
        "Open Camera", "Select from Gallery"
    )
    private lateinit var listView: ListView
    private lateinit var selectPhotoIntent: ActivityResultLauncher<Intent>
    private lateinit var selectedImageFile: File
    private val selectedImgFileName = "profile_photo_from_gallery.jpg"
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
//        selectPhotoIntent = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
//        {result: androidx.activity.result.ActivityResult ->
//            if(result.resultCode == Activity.RESULT_OK) {
//                handleSelectedImageFromGallery(result.data)
//            }
//        }
        photoButton.setOnClickListener {
            imageOptions()
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
        else if(userClass.text.toString().isEmpty() || userClass.text.toString() == "-1"){
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
        if(selectedImageFile.exists()){
            Files.copy(
                selectedImageFile.inputStream(),
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
//        selectedImageFile.delete()

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
        if(firstTimeLoad && savedImageUriString != null){
            bitmap = Utilities.getBitMap(this, Uri.parse(savedImageUriString))
            profilePhoto.setImageBitmap(bitmap)
        }
    }

    override fun onStop() {
        tempImageFile.delete()
        super.onStop()
    }
    private fun imageOptions(){
        dialogView = LayoutInflater.from(this).inflate(R.layout.photo_options, null)
        val dialogBuilder = AlertDialog.Builder(this).setView(dialogView).setCancelable(true)
        val dialog = dialogBuilder.create()
        dialog.show()
        listView = dialog.findViewById(R.id.profileImageOptionsListView)
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this,
            android.R.layout.simple_list_item_1, optionTitles)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener(){ parent: AdapterView<*>, view: View, position: Int, id: Long ->
            if (position == 0 ){
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri)
                takePhoto(intent)
                dialog.dismiss()
            }
            else if (position == 1){
//                chooseFromGallery()
                dialog.dismiss()
            }
        }
    }

    private fun chooseFromGallery(){
        val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        selectPhotoIntent.launch(intentToGallery)
        setProfilePhoto()
    }
//    private fun handleSelectedImageFromGallery(imageData:Intent?){
//        if (imageData != null) {
//            var selectedImageUri  = imageData.data
//            if(selectedImageUri != null) {
//                selectedImageFile = File(getExternalFilesDir(null),selectedImgFileName)
//                Log.d("SelectedImage", "File exists: ${selectedImageFile.exists()}")
//                val directory = selectedImageFile.parentFile
//                if(!directory.exists()){
//                    directory.mkdirs()
//                }
//                Log.d("SelectedImage", "URI: $selectedImageUri")
//                Log.d("SelectedImage", "File path: ${selectedImageFile.absolutePath}")
//                val inputStream = contentResolver.openInputStream(selectedImageUri)
////                Log.d("SelectedImage", "File exists: ${selectedImageFile.exists()}")
//                if(selectedImageFile.exists()) {
//                    Files.copy(
//                        inputStream,
//                        selectedImageFile.toPath(),
//                        StandardCopyOption.REPLACE_EXISTING
//                    )
//
//                }
//                Log.d("SelectedImage", "File exists: ${selectedImageFile.exists()}")
//                selectedImageUri = FileProvider.getUriForFile(this, "com.MyRuns", selectedImageFile)
//                val bitmap = Utilities.getBitMap(this, selectedImageUri)
//                viewProfilePhoto.photoOfUser.value = bitmap
//                profilePhoto.setImageBitmap(bitmap)
//            }
//        }
//    }

}