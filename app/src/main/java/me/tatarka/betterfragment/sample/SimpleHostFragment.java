package me.tatarka.betterfragment.sample;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.FragmentHost;

public class SimpleHostFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.simple_host);
        final FragmentHost host = findViewById(R.id.host);
        findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.setFragment(MyFragment.newInstance(1));
            }
        });
        findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.setFragment(MyFragment.newInstance(2));
            }
        });
    }
}
