package me.tatarka.betterfragment.state;

import androidx.annotation.NonNull;

public interface StateStoreOwner {
    @NonNull
    StateStore getStateStore();
}
