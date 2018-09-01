package me.tatarka.betterfragment.app;

import android.content.Intent;
import android.content.res.Configuration;

import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ActivityCallbackRegistry extends ActivityCallbacks implements ActivityCallbacksObserver {

    private final CopyOnWriteArrayList<ActivityCallbacksObserver> observers = new CopyOnWriteArrayList<>();

    @Override
    public void addObserver(@NonNull ActivityCallbacksObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(@NonNull ActivityCallbacksObserver observer) {
        observers.remove(observer);
    }

    @Override
    public final void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        for (ActivityCallbacksObserver observer : observers) {
            observer.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (ActivityCallbacksObserver observer : observers) {
            observer.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        for (ActivityCallbacksObserver observer : observers) {
            observer.onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        for (ActivityCallbacksObserver observer : observers) {
            observer.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        for (ActivityCallbacksObserver observer : observers) {
            observer.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onLowMemory() {
        for (ActivityCallbacksObserver observer : observers) {
            observer.onLowMemory();
        }
    }
}
