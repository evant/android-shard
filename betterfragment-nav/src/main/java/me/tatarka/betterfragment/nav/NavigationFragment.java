package me.tatarka.betterfragment.nav;

import android.os.Bundle;

import androidx.annotation.NavigationRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.Navigation;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.FragmentFactory;

public class NavigationFragment extends Fragment implements NavHost {

    private static final String ARG_GRAPH_ID = "graph_id";
    private static final String ARG_FRAGMENT_FACTORY_CLASS = "fragmentFactoryClass";
    private static final String STATE_NAV_CONTROLLER = "nav_controller";

    public static NavigationFragment newInstance(@NavigationRes int graphId) {
        return newInstance(graphId, null);
    }

    public static NavigationFragment newInstance(@NavigationRes int graphId, @Nullable Class<? extends FragmentFactory> fragmentFactoryClass) {
        NavigationFragment fragment = new NavigationFragment();
        fragment.getArgs().putInt(ARG_GRAPH_ID, graphId);
        if (fragmentFactoryClass != null) {
            fragment.getArgs().putString(ARG_FRAGMENT_FACTORY_CLASS, fragmentFactoryClass.getName());
        }
        return fragment;
    }

    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        navController = new NavController(getContext());
        Navigation.setViewNavController(getView(), navController);
        FragmentNavigator fragmentNavigator = new FragmentNavigator(getView());
        navController.getNavigatorProvider().addNavigator(fragmentNavigator);
        if (savedState == null) {
            navController.setGraph(getArgs().getInt(ARG_GRAPH_ID));
        } else {
            navController.restoreState(savedState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle(STATE_NAV_CONTROLLER, navController.saveState());
    }

    @NonNull
    @Override
    public NavController getNavController() {
        return navController;
    }
}
