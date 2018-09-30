package me.tatarka.shard.pager.test;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.shard.app.Shard;
import me.tatarka.shard.state.InstanceStateSaver;

public class TestShard extends Shard implements InstanceStateSaver<Bundle> {
    private static final String STATE = "state";

    public int state;
    public boolean createCalled;
    public boolean saveInstanceStateCalled;

    @Override
    public void onCreate() {
        super.onCreate();
        createCalled = true;
        getInstanceStateStore().add(STATE, this);
    }

    @Nullable
    @Override
    public Bundle onSaveInstanceState() {
        saveInstanceStateCalled = true;
        Bundle bundle = new Bundle();
        bundle.putInt(STATE, state);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle instanceState) {
        state = instanceState.getInt(STATE);

    }
}
