package me.tatarka.shard.sample

import android.view.View
import me.tatarka.shard.app.Shard
import me.tatarka.shard.app.ShardDialogHost
import javax.inject.Inject

class DialogHostShard @Inject constructor() :
    Shard() {

    private val dialogHost: ShardDialogHost = ShardDialogHost(this)

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.dialogs)
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
    }
}