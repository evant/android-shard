package me.tatarka.betterfragment.sample;

import android.util.Log;

import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {
    public MyViewModel() {
        Log.d("VIEWMODEL", "newInstance " + this);
    }

    @Override
    protected void onCleared() {
        Log.d("VIEWMODEL", "cleared " + this);
    }
}
