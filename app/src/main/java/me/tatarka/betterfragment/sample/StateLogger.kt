package me.tatarka.betterfragment.sample

import android.os.Bundle
import android.util.Log
import me.tatarka.betterfragment.state.StateSaver
import javax.inject.Inject

class StateLogger @Inject constructor() : StateSaver {
    override fun onSaveState(outState: Bundle) {
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE " + this)
    }

    override fun onRestoreState(instanceState: Bundle) {
        Log.d("LIFECYCLE", "ON_RESTORE_INSTANCE_STATE " + this)
    }
}
