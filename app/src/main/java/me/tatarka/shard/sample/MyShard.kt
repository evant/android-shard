package me.tatarka.shard.sample

import android.widget.TextView
import androidx.lifecycle.get
import me.tatarka.shard.app.Shard
import me.tatarka.shard.lifecycle.ViewModelProviders
import javax.inject.Inject

private const val KEY_NUMBER = "number"

open class MyShard @Inject constructor(
    private val lifecycleLogger: LifecycleLogger,
    private val stateLogger: InstanceStateLogger,
    private val callbacksLogger: CallbacksLogger
) : Shard() {

    fun withNumber(number: Int): Shard = apply {
        args.putInt(KEY_NUMBER, number)
    }

    override fun onCreate() {
        super.onCreate()
        activityCallbacks.addOnMultiWindowModeChangedCallback(callbacksLogger)
        activityCallbacks.addOnPictureInPictureModeChangedCallback(callbacksLogger)
        componentCallbacks.addOnConfigurationChangedListener(callbacksLogger)
        componentCallbacks.addOnTrimMemoryListener(callbacksLogger)
        lifecycle.addObserver(lifecycleLogger)
        ViewModelProviders.of(this).get<MyViewModel>()
        setContentView(R.layout.shard)
        requireViewById<TextView>(R.id.number).text = args.getInt(KEY_NUMBER).toString()

        instanceStateStore.add("KEY", stateLogger)
    }

}
