package me.tatarka.shard.host.test;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.junit.Test;
import org.junit.runner.RunWith;

import me.tatarka.shard.app.Shard;
import me.tatarka.shard.host.ShardPageHostUI;
import me.tatarka.shard.host.ui.test.R;
import me.tatarka.shard.wiget.ShardPageHost;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ShardPageHostUITest {

    public static class ShardPageHostBottomNavActivity extends AppCompatActivity {
        int onNavigationItemReselected = 0;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.shard_page_host_bottom_nav);

            ShardPageHost pageHost = findViewById(R.id.page_host);
            BottomNavigationView navigationView = findViewById(R.id.bottom_nav);
            pageHost.setAdapter(new ShardPageHost.Adapter() {
                @Override
                public Shard newInstance(int id) {
                    return new Shard();
                }
            });
            navigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
                @Override
                public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
                    onNavigationItemReselected += 1;
                }
            });
            ShardPageHostUI.setupWithPageHost(navigationView, pageHost);
        }
    }

    @Test
    public void bottomNavigationView_onItemReselected_does_not_happen_on_restore() {
        try (ActivityScenario<ShardPageHostBottomNavActivity> scenario = ActivityScenario.launch(ShardPageHostBottomNavActivity.class)) {
            scenario.moveToState(Lifecycle.State.RESUMED);
            scenario.recreate();
            scenario.onActivity(new ActivityScenario.ActivityAction<ShardPageHostBottomNavActivity>() {
                @Override
                public void perform(ShardPageHostBottomNavActivity activity) {
                    assertEquals(0, activity.onNavigationItemReselected);
                }
            });
        }
    }
}
