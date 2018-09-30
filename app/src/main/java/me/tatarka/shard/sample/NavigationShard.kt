package me.tatarka.shard.sample

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import me.tatarka.shard.app.Shard
import javax.inject.Inject

class NavigationShard @Inject constructor() : Shard(), NavInterface {

    lateinit var controller: NavController

    override fun onCreate() {
        super.onCreate()
        setContentView(R.layout.navigation)
        controller = findNavController(requireViewById(R.id.nav))
        requireViewById<Toolbar>(R.id.toolbar).setupWithNavController(controller)
        requireViewById<View>(R.id.root).setOnClickListener { controller.navigate(R.id.root) }
        requireViewById<View>(R.id.dest1).setOnClickListener { controller.navigate(R.id.dest1) }
        requireViewById<View>(R.id.dest2).setOnClickListener { controller.navigate(R.id.dest2) }
    }

    override fun onBackPressed(): Boolean = controller.popBackStack()

    override fun onNavigateUp(): Boolean = controller.navigateUp()
}
