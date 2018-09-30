package me.tatarka.shard.sample

import android.util.Log

import javax.inject.Inject
import javax.inject.Singleton

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

@Singleton
class LifecycleLogger @Inject constructor() : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onCreate(owner: LifecycleOwner, event: Lifecycle.Event) {
        Log.d("LIFECYCLE", "$event $owner")
    }
}
