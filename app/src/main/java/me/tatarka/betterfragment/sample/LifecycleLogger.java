package me.tatarka.betterfragment.sample;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

@Singleton
public class LifecycleLogger implements LifecycleObserver {

    @Inject
    public LifecycleLogger() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    public void onCreate(LifecycleOwner owner, Lifecycle.Event event) {
        Log.d("LIFECYCLE", event + " " + owner);
    }
}
