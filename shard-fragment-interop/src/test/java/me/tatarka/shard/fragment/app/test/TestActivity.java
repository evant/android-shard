package me.tatarka.shard.fragment.app.test;

import android.os.Bundle;

import androidx.annotation.Nullable;

import me.tatarka.shard.fragment.app.ShardFragmentActivity;
import me.tatarka.shard.fragment.test.R;
import me.tatarka.shard.wiget.ShardHost;

public class TestActivity extends ShardFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_shard_host);
    }

    public TestShard getShard() {
        ShardHost host = findViewById(R.id.host);
        return (TestShard) host.getShard();
    }
}
