package me.tatarka.betterfragment.state;

import androidx.annotation.NonNull;

public interface InstanceStateStoreOwner {
    @NonNull
    InstanceStateStore getInstanceStateStore();
}
