package me.tatarka.betterfragment.sample;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.ui.NavigationUI;
import me.tatarka.betterfragment.Fragment;

import static androidx.navigation.Navigation.findNavController;

public class NavigationFragment extends Fragment {
    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.navigation);
        final NavController controller = findNavController(findViewForId(R.id.nav));
        Toolbar toolbar = findViewForId(R.id.toolbar);
        NavigationUI.setupWithNavController(toolbar, controller);
        findViewForId(R.id.root).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.root);
            }
        });
        findViewForId(R.id.dest1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.dest1);
            }
        });
        findViewForId(R.id.dest2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.dest2);
            }
        });
    }
}
