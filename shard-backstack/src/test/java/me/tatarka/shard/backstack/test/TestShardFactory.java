package me.tatarka.shard.backstack.test;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.shard.app.Shard;

public class TestShardFactory implements Shard.Factory {

    public final List<Shard> createdShards = new ArrayList<>();

    @NonNull
    @Override
    public <T extends Shard> T newInstance(@NonNull String name) {
        T shard = Shard.DefaultFactory.getInstance().newInstance(name);
        createdShards.add(shard);
        return shard;
    }
}
