package me.tatarka.shard.sample

import androidx.core.view.ViewCompat
import me.tatarka.shard.app.Shard
import javax.inject.Inject

class ShardSquareRight @Inject constructor() : Shard() {
    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.shard_square_right)
        ViewCompat.setTransitionName(requireViewById(R.id.square_right), "square")
    }
}