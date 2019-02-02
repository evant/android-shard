package me.tatarka.shard.sample

import android.content.Context
import android.widget.TextView
import androidx.annotation.ContentView
import androidx.appcompat.app.AlertDialog
import me.tatarka.shard.appcompat.app.AlertDialogShard
import javax.inject.Inject

private const val KEY_CUSTOM_VIEW = "custom_view"
private const val KEY_NUMBER = "number"

class MyAlertDialogShard @Inject constructor() : AlertDialogShard() {

    fun withCustomView(number: Int) = apply {
        args.putBoolean(KEY_CUSTOM_VIEW, true)
        args.putInt(KEY_NUMBER, number)
    }

    override fun onBuildAlertDialog(context: Context): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle("Title")
            .setMessage("Message")
            .setPositiveButton("Ok", null)
            .setNegativeButton("Nah", null);
    }

    override fun onCreate() {
        if (args.getBoolean(KEY_CUSTOM_VIEW)) {
            setContentView(R.layout.dialog)
            requireViewById<TextView>(R.id.number).text = args.getInt(KEY_NUMBER).toString()
        }
    }
}