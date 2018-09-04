package me.tatarka.betterfragment.sample

import android.widget.TextView
import me.tatarka.betterfragment.app.DialogFragment
import javax.inject.Inject

private const val KEY_NUMBER = "number"

class SimpleDialogFragment @Inject constructor() : DialogFragment() {

    fun withNumber(number: Int): SimpleDialogFragment = apply {
        args.putInt(KEY_NUMBER, number)
    }

    override fun onCreate() {
        setContentView(R.layout.dialog)
        requireViewById<TextView>(R.id.number).text = args.getInt(KEY_NUMBER).toString()
    }
}