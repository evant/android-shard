package me.tatarka.shard.sample

import android.os.Parcelable
import android.util.Log
import me.tatarka.shard.state.InstanceStateSaver
import javax.inject.Inject

class InstanceStateLogger @Inject constructor() :
    InstanceStateSaver<Parcelable> {
    override fun onSaveInstanceState(): Parcelable? {
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE " + this)
        return null
    }

    override fun onRestoreInstanceState(instanceState: Parcelable) {
        Log.d("LIFECYCLE", "ON_RESTORE_INSTANCE_STATE " + this)
    }
}
