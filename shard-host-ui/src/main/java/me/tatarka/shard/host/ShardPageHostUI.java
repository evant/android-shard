package me.tatarka.shard.host;

import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import me.tatarka.shard.wiget.ShardPageHost;

public final class ShardPageHostUI {
    private ShardPageHostUI() {
    }

    public static void setupWithPageHost(final NavigationView view, final ShardPageHost host) {
        view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                host.setCurrentPage(menuItem.getItemId());
                return true;
            }
        });
        host.setOnPageChangedListener(new ShardPageHost.OnPageChangedListener() {
            @Override
            public void onPageChanged(int id) {
                view.setCheckedItem(id);
            }
        });
    }

    public static void setupWithPageHost(final BottomNavigationView view, final ShardPageHost host) {
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                host.setCurrentPage(menuItem.getItemId());
                return true;
            }
        });
        host.setOnPageChangedListener(new ShardPageHost.OnPageChangedListener() {
            @Override
            public void onPageChanged(int id) {
                view.setSelectedItemId(id);
            }
        });
    }
}
