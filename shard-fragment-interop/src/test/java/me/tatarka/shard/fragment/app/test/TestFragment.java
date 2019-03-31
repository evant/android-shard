package me.tatarka.shard.fragment.app.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import me.tatarka.shard.fragment.test.R;

public class TestFragment extends Fragment {

    public boolean onCreateCalled;
    public boolean onCreateViewCalled;
    public boolean onActivityCreatedCalled;
    public boolean onDestroyCalled;
    public int state;
    public int resultCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            state = savedInstanceState.getInt("state");
        }
        onCreateCalled = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        onCreateViewCalled = true;
        return inflater.inflate(R.layout.test_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onActivityCreatedCalled = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onDestroyCalled = true;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("state", state);
    }

    public void startActivityForResult(Class<? extends Activity> activity) {
        startActivityForResult(new Intent(getContext(), activity), 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        this.resultCode = resultCode;
    }
}
