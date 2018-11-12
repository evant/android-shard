package me.tatarka.shard.sample

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.Button
import me.tatarka.shard.app.Shard
import me.tatarka.shard.app.ShardDialogHost
import javax.inject.Inject

const val REQUEST_CODE_ACTIVITY = 1
const val REQUEST_CODE_PERMISSION = 2

class DialogHostShard @Inject constructor() :
    Shard() {

    private val dialogHost: ShardDialogHost = ShardDialogHost(this)

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.dialogs)
        activityCallbacks.addOnActivityResultCallback(REQUEST_CODE_ACTIVITY) { resultCode, _ ->
            requireViewById<Button>(R.id.start_activity_for_result).text =
                    "Result: ${if (resultCode == Activity.RESULT_OK) "Ok" else "Cancel"}"
        }
        activityCallbacks.addOnRequestPermissionResultCallback(REQUEST_CODE_PERMISSION) { _, grantResults ->
            requireViewById<Button>(R.id.request_permission).text =
                    "Result: ${if (grantResults[0] == PackageManager.PERMISSION_GRANTED) "Granted" else "Denied"}"
        }
        requireViewById<View>(R.id.simple_dialog).setOnClickListener {
            dialogHost.show(
                shardFactory.newInstance<SimpleDialogShard>()
                    .withNumber(1)
            )
        }
        requireViewById<View>(R.id.alert_dialog).setOnClickListener {
            dialogHost.show(shardFactory.newInstance<MyAlertDialogShard>())
        }
        requireViewById<View>(R.id.alert_dialog_custom_view).setOnClickListener {
            dialogHost.show(
                shardFactory.newInstance<MyAlertDialogShard>()
                    .withCustomView(2)
            )
        }
        requireViewById<View>(R.id.start_activity_for_result).setOnClickListener {
            activityCallbacks.startActivityForResult(
                Intent(context, ResultActivity::class.java),
                REQUEST_CODE_ACTIVITY
            )
        }
        requireViewById<View>(R.id.request_permission).setOnClickListener {
            activityCallbacks.requestPermissions(
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_CODE_PERMISSION
            )
        }
    }
}