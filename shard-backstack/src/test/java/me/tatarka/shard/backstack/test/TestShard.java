package me.tatarka.shard.backstack.test;

import android.view.View;

import androidx.annotation.NonNull;

import me.tatarka.shard.app.Shard;

public class TestShard extends Shard {

    public static TestShard create(String name) {
        TestShard testShard = new TestShard();
        testShard.getArgs().putString("name", name);
        return testShard;
    }

    @Override
    public void onCreate() {
        setContentView(new View(getContext()));
    }

    public String getName() {
        return getArgs().getString("name");
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString() + " " + getName();
    }
}
