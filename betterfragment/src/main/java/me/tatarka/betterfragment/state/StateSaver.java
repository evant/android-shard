package me.tatarka.betterfragment.state;

import android.os.Bundle;

import androidx.annotation.NonNull;

public interface StateSaver {

    void onSaveState(@NonNull Bundle outState);

    void onRestoreState(@NonNull Bundle instanceState);
}
