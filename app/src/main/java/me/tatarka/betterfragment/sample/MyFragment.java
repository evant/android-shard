package me.tatarka.betterfragment.sample;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import me.tatarka.betterfragment.Fragment;
import me.tatarka.betterfragment.ViewModelProviders;

public class MyFragment extends Fragment {
    private static final String KEY_NUMBER = "number";

    public static MyFragment newInstance(int number) {
        MyFragment fragment = new MyFragment();
        fragment.getArgs().putInt(KEY_NUMBER, number);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedState) {
        super.onCreate(savedState);
        getLifecycle().addObserver(new LifecycleLogger());
        ViewModelProviders.of(this).get(MyViewModel.class);
        setContentView(R.layout.fragment);
        TextView number = findViewForId(R.id.number);
        number.setText("" + getArgs().getInt(KEY_NUMBER));
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        Log.d("LIFECYCLE", "ON_SAVE_INSTANCE_STATE " + this);
    }
}
