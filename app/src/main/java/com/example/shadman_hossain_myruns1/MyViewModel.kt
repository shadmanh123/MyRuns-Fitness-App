package com.example.shadman_hossain_myruns1


import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {
    val photoOfUser = MutableLiveData<Bitmap>()
}