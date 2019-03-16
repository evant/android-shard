package me.tatarka.shard.nav;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;

public class TestNavActivity extends Activity {

    public NavController controller;
    public TestOptimizingNavigator navigator;
    public NavGraph navGraph;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = new NavController(this);
        navigator = new TestOptimizingNavigator();
        controller.getNavigatorProvider().addNavigator(navigator);
        controller.addOnDestinationChangedListener(navigator);
        navGraph = new NavGraph(controller.getNavigatorProvider().getNavigator(NavGraphNavigator.class));
    }
}
