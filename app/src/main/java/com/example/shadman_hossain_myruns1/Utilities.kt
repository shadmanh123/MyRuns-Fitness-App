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
import android.webkit.PermissionRequest
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

object Utilities {
    fun checkForPermission(activity: Activity?){
        if(Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA), 0)
        }
    }

    fun getBitMap(context: Context, imgUri: Uri): Bitmap{
        val inputStream = context.contentResolver.openInputStream(imgUri)
        var bitmap = BitmapFactory.decodeStream(inputStream)
        val matrix = Matrix()
        var ret = Bitmap.createBitmap(bitmap, 0,0,bitmap.width,bitmap.height, matrix,true)
        return ret
    }
}