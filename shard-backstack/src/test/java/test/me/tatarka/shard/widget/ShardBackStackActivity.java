package test.me.tatarka.shard.widget;

import android.os.Bundle;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;

import me.tatarka.shard.widget.ShardBackStackHost;

public class ShardBackStackActivity extends ComponentActivity {

    public ShardBackStackHost host;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        host = new ShardBackStackHost(this);
        setContentView(host);
    }
}
