package me.tatarka.shard.sample

import android.os.Parcelable
import android.util.Log
import me.tatarka.shard.savedstate.SavedStateProvider
import javax.inject.Inject

class InstanceStateLogger @Inject constructor() :
        SavedStateProvider<Parcelable> {
    override fun saveState(): Parcelable? {
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE " + this)
        return null
    }

    override fun restoreState(instanceState: Parcelable) {
        Log.d("LIFECYCLE", "ON_RESTORE_INSTANCE_STATE " + this)
    }
}
