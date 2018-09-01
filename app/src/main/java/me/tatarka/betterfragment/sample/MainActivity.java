package me.tatarka.betterfragment.sample;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.navigation.ui.NavigationUI;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.host.FragmentPageHostUI;
import me.tatarka.betterfragment.sample.dagger.DaggerAppComponent;
import me.tatarka.betterfragment.sample.dagger.DaggerFragmentFactory;
import me.tatarka.betterfragment.widget.FragmentPageHost;

public class MainActivity extends AppCompatActivity {

    @Inject
    DaggerFragmentFactory fragmentFactory;

    private FragmentPageHost pageHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerAppComponent.create().inject(this);

        setContentView(R.layout.activity_main);
        pageHost = findViewById(R.id.page_host);
        final BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        pageHost.setAdapter(new FragmentPageHost.Adapter() {
            @Nullable
            @Override
            public Fragment newInstance(int id) {
                switch (id) {
                    case R.id.simple_host:
                        return fragmentFactory.newInstance(SimpleHostFragment.class);
                    case R.id.view_pager:
                        return fragmentFactory.newInstance(ViewPagerFragment.class);
                    case R.id.navigation:
                        return fragmentFactory.newInstance(NavigationFragment.class);
                }
                return null;
            }
        });
        FragmentPageHostUI.setupWithBottomNavigationView(pageHost, bottomNav);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = pageHost.getFragment();
        if (!(fragment instanceof NavInterface && ((NavInterface) fragment).onBackPressed())) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigateUp() {
        Fragment fragment = pageHost.getFragment();
        if (fragment instanceof NavInterface && ((NavInterface) fragment).onNavigateUp()) {
            return true;
        } else {
            return super.onNavigateUp();
        }
    }
}
