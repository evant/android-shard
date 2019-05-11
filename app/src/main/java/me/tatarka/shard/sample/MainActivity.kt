package me.tatarka.shard.sample

import android.os.Bundle
import androidx.lifecycle.get
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.tatarka.shard.app.newInstance
import me.tatarka.shard.appcompat.app.ShardAppCompatActivity
import me.tatarka.shard.host.setupWithPageHost
import me.tatarka.shard.lifecycle.ViewModelProviders
import me.tatarka.shard.sample.dagger.injector
import me.tatarka.shard.wiget.ShardPageHost
import me.tatarka.shard.wiget.ShardPageHost.Adapter

class MainActivity : ShardAppCompatActivity() {

    lateinit var pageHost: ShardPageHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shardFactory = LeakLifecycleWatcher.wrap(injector.shardFactory)

        ViewModelProviders.of(this).get<MyViewModel>()

        setContentView(R.layout.activity_main)
        pageHost = findViewById(R.id.page_host)
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)!!
        pageHost.adapter = Adapter { id ->
            when (id) {
                R.id.simple_host -> shardFactory.newInstance<SimpleHostShard>()
                R.id.view_pager -> shardFactory.newInstance<ViewPagerShard>()
                R.id.navigation -> shardFactory.newInstance<NavigationShard>()
                R.id.dialogs -> shardFactory.newInstance<DialogHostShard>()
                else -> null
            }
        }
        bottomNav.setupWithPageHost(pageHost)

        bottomNav.setOnNavigationItemReselectedListener { item ->
            val shard = pageHost.shard
            if (shard is Resetable) {
                shard.reset()
            }
        }
    }
}
