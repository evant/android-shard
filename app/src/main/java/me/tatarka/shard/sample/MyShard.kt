package me.tatarka.shard.sample

import android.app.Application
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ContentView
import androidx.lifecycle.SavedStateVMFactory
import androidx.lifecycle.get
import me.tatarka.shard.app.Shard
import me.tatarka.shard.lifecycle.ViewModelProviders
import javax.inject.Inject

private const val KEY_NUMBER = "number"

@ContentView(R.layout.shard)
class MyShard @Inject constructor(
        private val lifecycleLogger: LifecycleLogger,
        private val stateLogger: InstanceStateLogger,
        private val callbacksLogger: CallbacksLogger
) : Shard() {

    fun withNumber(number: Int): Shard = apply {
        args.putInt(KEY_NUMBER, number)
    }

    override fun onCreate() {
        activityCallbacks.addOnMultiWindowModeChangedCallback(callbacksLogger)
        activityCallbacks.addOnPictureInPictureModeChangedCallback(callbacksLogger)
        componentCallbacks.addOnConfigurationChangedListener(callbacksLogger)
        componentCallbacks.addOnTrimMemoryListener(callbacksLogger)
        lifecycle.addObserver(lifecycleLogger)
        val vm = ViewModelProviders.of(this, SavedStateVMFactory(context.applicationContext as Application, this, args)).get<MyViewModel>()
        requireViewById<TextView>(R.id.number).text = args.getInt(KEY_NUMBER).toString()
        requireViewById<EditText>(R.id.saved_text).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                vm.savedField.value = s.toString()
            }
        })

        stateLogger.register(savedStateRegistry, "KEY")
    }

}
