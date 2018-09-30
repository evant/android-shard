package me.tatarka.shard.sample

import android.widget.TextView
import me.tatarka.shard.app.DialogShard
import javax.inject.Inject

private const val KEY_NUMBER = "number"

class SimpleDialogShard @Inject constructor() : DialogShard() {

    fun withNumber(number: Int): SimpleDialogShard = apply {
        args.putInt(KEY_NUMBER, number)
    }

    override fun onCreate() {
        setContentView(R.layout.dialog)
        requireViewById<TextView>(R.id.number).text = args.getInt(KEY_NUMBER).toString()
    }
}