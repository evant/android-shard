package me.tatarka.betterfragment.app;

import android.content.Intent;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface ActivityCallbacksObserver {

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    void onMultiWindowModeChanged(boolean isInMultiWindowMode);

    void onPictureInPictureModeChanged(boolean isInPictureInPictureMode);

    void onConfigurationChanged(@NonNull Configuration newConfig);

    void onLowMemory();
}
