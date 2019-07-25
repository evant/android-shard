package me.tatarka.shard.sample

import androidx.viewpager2.widget.ViewPager2
import me.tatarka.shard.app.Shard
import me.tatarka.shard.app.newInstance
import me.tatarka.shard.pager2.ShardAdapter
import javax.inject.Inject

class ViewPagerShard @Inject constructor() : Shard(), Resetable {
    lateinit var pager: ViewPager2

    override fun onCreate() {
        setContentView(R.layout.view_pager)
        pager = requireViewById(R.id.pager)
        val pager: ViewPager2 = requireViewById(R.id.pager)
        pager.adapter = object : ShardAdapter(this) {
            override fun createShard(position: Int): Shard {
                return shardFactory.newInstance<MyShard>().withNumber(position)
            }

            override fun getItemCount(): Int {
                return 10
            }
        }
    }

    override fun reset() {
        pager.currentItem = 0
    }
}
