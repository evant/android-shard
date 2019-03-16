@file:Suppress("NOTHING_TO_INLINE")

package me.tatarka.shard.app

import android.content.Context

inline fun ShardOwner.showDialog(shard: BaseDialogShard) {
    ShardDialogHost.getInstance(this).show(shard)
}

inline fun Context.showDialog(shard: BaseDialogShard) {
    ShardDialogHost.getInstance(this).show(shard)
}