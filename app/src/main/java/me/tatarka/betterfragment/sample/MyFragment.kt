package me.tatarka.betterfragment.sample

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import me.tatarka.betterfragment.app.Fragment
import javax.inject.Inject

private const val KEY_NUMBER = "number"

class MyFragment @Inject constructor(private val lifecycleLogger: LifecycleLogger) : Fragment() {

    fun withNumber(number: Int): Fragment = apply {
        args.putInt(KEY_NUMBER, number)
    }

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        lifecycle.addObserver(lifecycleLogger)
        viewModel<MyViewModel>()
        setContentView(R.layout.fragment)
        requireViewById<TextView>(R.id.number).text = args.getInt(KEY_NUMBER).toString()
    }

    override fun onSaveInstanceState(state: Bundle) {
        super.onSaveInstanceState(state)
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE " + this)
    }
}
