package me.tatarka.shard.content;

import android.content.ComponentCallbacks2;
import android.content.res.Configuration;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

public interface ComponentCallbacks {
    interface OnConfigurationChangedListener {
        /**
         * Called by the system when the device configuration changes while your
         * activity is running.  Note that this will <em>only</em> be called if
         * you have selected configurations you would like to handle with the
         * {@link android.R.attr#configChanges} attribute in your manifest.  If
         * any configuration change occurs that is not selected to be reported
         * by that attribute, then instead of reporting it the system will stop
         * and restart the activity (to have it launched with the new
         * configuration).
         *
         * <p>At the time that this function has been called, your Resources
         * object will have been updated to return resource values matching the
         * new configuration.
         *
         * @param newConfig The new device configuration.
         */
        void onConfigurationChanged(@NonNull Configuration newConfig);
    }

    void addOnConfigurationChangedListener(@NonNull OnConfigurationChangedListener listener);

    void removeOnConfigurationChangedListener(@NonNull OnConfigurationChangedListener listener);

    interface OnTrimMemoryListener {
        /**
         * Called when the operating system has determined that it is a good
         * time for a process to trim unneeded memory from its process.  This will
         * happen for example when it goes in the background and there is not enough
         * memory to keep as many background processes running as desired.  You
         * should never compare to exact values of the level, since new intermediate
         * values may be added -- you will typically want to compare if the value
         * is greater or equal to a level you are interested in.
         *
         * <p>To retrieve the processes current trim level at any point, you can
         * use {@link android.app.ActivityManager#getMyMemoryState
         * ActivityManager.getMyMemoryState(RunningAppProcessInfo)}.
         *
         * @param level The context of the trim, giving a hint of the amount of
         *              trimming the application may like to perform.
         */
        void onTrimMemory(@TrimMemoryLevel int level);
    }

    void addOnTrimMemoryListener(@NonNull OnTrimMemoryListener listener);

    void removeOnTrimMemoryListener(@NonNull OnTrimMemoryListener listener);

    @IntDef(value = {
            ComponentCallbacks2.TRIM_MEMORY_COMPLETE,
            ComponentCallbacks2.TRIM_MEMORY_MODERATE,
            ComponentCallbacks2.TRIM_MEMORY_BACKGROUND,
            ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW,
            ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE,
    })
    @Retention(RetentionPolicy.SOURCE)
    @interface TrimMemoryLevel {
    }
}
