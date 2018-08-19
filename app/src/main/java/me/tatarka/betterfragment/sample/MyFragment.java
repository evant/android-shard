package me.tatarka.betterfragment.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.ViewModelProviders;

public class MyFragment extends Fragment {
    private static final String KEY_NUMBER = "number";
    private final LifecycleLogger lifecycleLogger;

    @Inject
    public MyFragment(LifecycleLogger lifecycleLogger) {
        this.lifecycleLogger = lifecycleLogger;
    }

    public Fragment withNumber(int number) {
        getArgs().putInt(KEY_NUMBER, number)
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        getLifecycle().addObserver(lifecycleLogger);
        ViewModelProviders.of(this).get(MyViewModel.class);
        setContentView(R.layout.fragment);
        TextView number = findViewById(R.id.number);
        number.setText("" + getArgs().getInt(KEY_NUMBER));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE " + this);
    }
}
