package me.tatarka.shard.fragment.app.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.savedstate.SavedStateRegistry;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.fragment.test.R;

import static me.tatarka.shard.fragment.app.ShardFragmentManager.getFragmentManager;

public class TestShard extends Shard {
    public boolean onCreateCalled;
    public int state;
    public int resultCode;
    public TestFragment fragment;

    @Override
    public void onCreate() {
        onCreateCalled = true;
        setContentView(R.layout.test_shard);

        Bundle bundle = getSavedStateRegistry().consumeRestoredStateForKey("state");
        if (bundle != null) {
            state = bundle.getInt("state");
        }
        getSavedStateRegistry().registerSavedStateProvider("state", new SavedStateRegistry.SavedStateProvider() {
            @NonNull
            @Override
            public Bundle saveState() {
                Bundle bundle = new Bundle();
                bundle.putInt("state", state);
                return bundle;
            }
        });
        getActivityCallbacks().addOnActivityResultCallback(2, new ActivityCallbacks.OnActivityResultCallback() {
            @Override
            public void onActivityResult(int resultCode, @Nullable Intent data) {
                TestShard.this.resultCode = resultCode;
            }
        });

        FragmentManager fm = getFragmentManager(this);
        if ((fragment = (TestFragment) fm.findFragmentById(R.id.content)) == null) {
            fragment = new TestFragment();
            fm.beginTransaction().add(R.id.content, fragment).commitNow();
        }
    }

    public void startActivityForResult(Class<? extends Activity> activity) {
        getActivityCallbacks().startActivityForResult(new Intent(getContext(), activity), 2);
    }
}
