package me.tatarka.shard.test;

import android.view.View;

import me.tatarka.shard.app.Shard;

public class TestShard extends Shard {
    public boolean createCalled;

    @Override
    public void onCreate() {
        createCalled = true;
        setContentView(new View(getContext()));
    }
}
