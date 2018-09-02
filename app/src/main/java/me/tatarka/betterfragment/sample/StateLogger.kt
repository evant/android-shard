package me.tatarka.betterfragment.sample

import android.os.Parcelable
import android.util.Log
import me.tatarka.betterfragment.state.StateSaver
import javax.inject.Inject

class StateLogger @Inject constructor() : StateSaver<Parcelable?> {

    override fun restoreState(state: Parcelable?) {
        Log.d("LIFECYCLE", "ON_RESTORE_INSTANCE_STATE " + this)
    }

    override fun saveState(): Parcelable? {
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE " + this)
        return null
    }
}
