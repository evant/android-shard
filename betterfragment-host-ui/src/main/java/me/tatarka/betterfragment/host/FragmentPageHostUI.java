package me.tatarka.betterfragment.host;

import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import me.tatarka.betterfragment.widget.FragmentPageHost;

public final class FragmentPageHostUI {
    private FragmentPageHostUI() {
    }

    public static void setupWithNavigationView(final FragmentPageHost host, final NavigationView view) {
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

    public static void setupWithBottomNavigationView(final FragmentPageHost host, final BottomNavigationView view) {
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
