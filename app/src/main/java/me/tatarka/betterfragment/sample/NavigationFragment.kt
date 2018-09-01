package me.tatarka.betterfragment.sample

import android.os.Bundle
import android.view.View

import javax.inject.Inject
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import me.tatarka.betterfragment.app.Fragment

import androidx.navigation.Navigation.findNavController

class NavigationFragment @Inject constructor() : Fragment(), NavInterface {

    lateinit var controller: NavController

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        setContentView(R.layout.navigation)
        controller = findNavController(requireViewById(R.id.nav))
        NavigationUI.setupWithNavController(requireViewById<Toolbar>(R.id.toolbar), controller)
        requireViewById<View>(R.id.root).setOnClickListener { controller.navigate(R.id.root) }
        requireViewById<View>(R.id.dest1).setOnClickListener { controller.navigate(R.id.dest1) }
        requireViewById<View>(R.id.dest2).setOnClickListener { controller.navigate(R.id.dest2) }
    }

    override fun onBackPressed(): Boolean = controller.popBackStack()

    override fun onNavigateUp(): Boolean = controller.navigateUp()
}
