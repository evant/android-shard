package me.tatarka.shard.sample

import androidx.core.view.ViewCompat
import me.tatarka.shard.app.Shard
import javax.inject.Inject

class ShardSquareLeft @Inject constructor() : Shard() {
    override fun onCreate() {
        setContentView(R.layout.shard_square_left)
        ViewCompat.setTransitionName(requireViewById(R.id.square_left), "square")
    }
}