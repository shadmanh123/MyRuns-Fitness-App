package com.example.shadman_hossain_myruns1

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Utilities {
    fun checkForCameraPermission(activity: Activity?){
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0)
        }
    }

    fun checkForGPSPermission(activity: Activity?): Boolean{
        activity as MapDisplayActivity
        if (Build.VERSION.SDK_INT < 23){
            return true
        }
        if(ContextCompat.checkSelfPermission(activity.applicationContext,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
            return false
        }
        return true
    }

    fun getBitMap(context: Context, imgUri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(imgUri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val matrix = Matrix()
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

}