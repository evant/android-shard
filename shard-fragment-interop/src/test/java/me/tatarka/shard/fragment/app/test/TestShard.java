package me.tatarka.shard.fragment.app.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.savedstate.SavedStateRegistry;

import me.tatarka.shard.activity.ActivityCallbacks;
import me.tatarka.shard.app.Shard;

public class TestShard extends Shard {
    public boolean onCreateCalled;
    public int state;
    public int resultCode;

    @Override
    public void onCreate() {
        onCreateCalled = true;

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
    }

    public void startActivityForResult() {
        getActivityCallbacks().startActivityForResult(new Intent(getContext(), ShardFragmentTest.ResultActivity.class), 2);
    }
}
