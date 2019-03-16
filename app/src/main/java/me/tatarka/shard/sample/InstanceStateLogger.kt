package me.tatarka.shard.sample

import android.os.Bundle
import android.util.Log
import androidx.savedstate.SavedStateRegistry
import javax.inject.Inject

class InstanceStateLogger @Inject constructor() : SavedStateRegistry.SavedStateProvider {

    fun register(registry: SavedStateRegistry, key: String) {
        val bundle = registry.consumeRestoredStateForKey(key)
        if (bundle != null) {
            Log.d("LIFECYCLE", "ON_RESTORE_INSTANCE_STATE $this")
        }
        registry.registerSavedStateProvider(key, this)
    }

    override fun saveState(): Bundle {
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE $this")
        return Bundle.EMPTY
    }
}
