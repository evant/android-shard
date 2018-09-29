package me.tatarka.betterfragment.sample

import android.widget.TextView
import androidx.lifecycle.get
import me.tatarka.betterfragment.app.Fragment
import me.tatarka.betterfragment.lifecycle.ViewModelProviders
import javax.inject.Inject

private const val KEY_NUMBER = "number"

open class MyFragment @Inject constructor(
    private val lifecycleLogger: LifecycleLogger,
    private val stateLogger: InstanceStateLogger
) : Fragment() {

    fun withNumber(number: Int): Fragment = apply {
        args.putInt(KEY_NUMBER, number)
    }

    override fun onCreate() {
        super.onCreate()
        lifecycle.addObserver(lifecycleLogger)
        ViewModelProviders.of(this).get<MyViewModel>()
        setContentView(R.layout.fragment)
        requireViewById<TextView>(R.id.number).text = args.getInt(KEY_NUMBER).toString()

        instanceStateStore.add("KEY", stateLogger)
    }

}
