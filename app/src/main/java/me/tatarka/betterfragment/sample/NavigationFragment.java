package me.tatarka.betterfragment.sample;

import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;
import me.tatarka.betterfragment.Fragment;

import static androidx.navigation.Navigation.findNavController;

public class NavigationFragment extends Fragment {

    @Inject
    public NavigationFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.navigation);
        final NavController controller = findNavController(requireViewById(R.id.nav));
        Toolbar toolbar = requireViewById(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, controller);
        requireViewById(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.root);
            }
        });
        requireViewById(R.id.dest1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.dest1);
            }
        });
        requireViewById(R.id.dest2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.dest2);
            }
        });
    }
}
