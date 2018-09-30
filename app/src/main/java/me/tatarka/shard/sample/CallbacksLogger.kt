package me.tatarka.shard.sample

import android.content.res.Configuration
import android.util.Log
import me.tatarka.shard.app.ActivityCallbacks
import me.tatarka.shard.content.ComponentCallbacks
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallbacksLogger @Inject constructor() : ComponentCallbacks.OnConfigurationChangedListener,
    ComponentCallbacks.OnTrimMemoryListener, ActivityCallbacks.OnMultiWindowModeChangedListener,
    ActivityCallbacks.OnPictureInPictureModeChangedListener {
    override fun onConfigurationChanged(newConfig: Configuration) {
        Log.d("CALLBACKS", "onConfigurationChanged $newConfig")
    }

    override fun onTrimMemory(level: Int) {
        Log.d("CALLBACKS", "onTrimMemory $level")
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean) {
        Log.d("CALLBACKS", "onMultiWindowModeChanged $isInMultiWindowMode")
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        Log.d("CALLBACKS", "onPictureInPictureModeChanged $isInPictureInPictureMode")
    }
}