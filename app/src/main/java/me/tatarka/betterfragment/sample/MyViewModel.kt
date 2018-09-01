package me.tatarka.betterfragment.sample

import android.util.Log

import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    init {
        Log.d("VIEWMODEL", "newInstance ${this}")
    }

    override fun onCleared() {
        Log.d("VIEWMODEL", "cleared ${this}")
    }
}
