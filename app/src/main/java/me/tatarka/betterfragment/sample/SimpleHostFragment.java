package me.tatarka.betterfragment.sample;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import me.tatarka.betterfragment.app.Fragment;
import me.tatarka.betterfragment.widget.FragmentHost;

public class SimpleHostFragment extends Fragment {

    private final Factory fragmentFactory;

    @Inject
    public SimpleHostFragment(Factory factory) {
        fragmentFactory = factory;
    }

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.simple_host);
        final FragmentHost host = requireViewById(R.id.host);
        requireViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.setFragment(fragmentFactory.newInstance(MyFragment.class).withNumber(1));
            }
        });
        requireViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                host.setFragment(fragmentFactory.newInstance(MyFragment.class).withNumber(2));
            }
        });
    }
}
