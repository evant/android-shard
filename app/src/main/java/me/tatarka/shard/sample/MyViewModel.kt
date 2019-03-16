package me.tatarka.shard.sample

import android.util.Log
import androidx.lifecycle.*

class MyViewModel @JvmOverloads constructor(savedStateHandle: SavedStateHandle? = null) : ViewModel(), Observer<String> {

    val savedField: MutableLiveData<String>

    init {
        Log.d("VIEWMODEL", "newInstance ${this}")
        savedField = savedStateHandle?.getLiveData<String>("key") ?: MutableLiveData()
        savedField.observeForever(this)
        Log.d("VIEWMODEL", "field initial value: ${savedField.value} ${this}")
    }

    override fun onCleared() {
        Log.d("VIEWMODEL", "cleared ${this}")
        savedField.removeObserver(this)
    }

    override fun onChanged(value: String) {
        Log.d("VIEWMODEL", "field changed: $value ${this}")
    }
}
