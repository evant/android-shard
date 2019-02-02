package me.tatarka.shard.sample

import androidx.annotation.ContentView
import androidx.core.view.ViewCompat
import me.tatarka.shard.app.Shard
import javax.inject.Inject

@ContentView(R.layout.shard_square_middle)
class ShardSquareMiddle @Inject constructor() : Shard() {
    override fun onCreate() {
        ViewCompat.setTransitionName(requireViewById(R.id.square_middle), "square")
    }
}