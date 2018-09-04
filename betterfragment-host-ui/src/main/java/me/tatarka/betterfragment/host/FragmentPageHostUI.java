package me.tatarka.betterfragment.host;

import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import me.tatarka.betterfragment.wiget.FragmentPageHost;

public final class FragmentPageHostUI {
    private FragmentPageHostUI() {
    }

    public static void setupWithPageHost(final NavigationView view, final FragmentPageHost host) {
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                host.setCurrentPage(menuItem.getItemId());
                return true;
            }
        });
        host.setOnPageChangedListener(new FragmentPageHost.OnPageChangedListener() {
            @Override
            public void onPageChanged(int id) {
                view.setCheckedItem(id);
            }
        });
    }

    public static void setupWithPageHost(final BottomNavigationView view, final FragmentPageHost host) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                host.setCurrentPage(menuItem.getItemId());
                return true;
            }
        });
        host.setOnPageChangedListener(new FragmentPageHost.OnPageChangedListener() {
            @Override
            public void onPageChanged(int id) {
                view.setSelectedItemId(id);
            }
        });
    }
}
