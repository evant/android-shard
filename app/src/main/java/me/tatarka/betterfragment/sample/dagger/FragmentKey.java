package me.tatarka.betterfragment.sample.dagger;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import dagger.MapKey;
import me.tatarka.betterfragment.Fragment;

@MapKey
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentKey {
    Class<? extends Fragment> value();
}
