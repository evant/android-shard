package me.tatarka.betterfragment.sample.dagger;

import javax.inject.Singleton;

import dagger.Component;
import me.tatarka.betterfragment.sample.SimpleHostFragment;

@Singleton
@Component(modules = FragmentModule.class)
public interface AppComponent {
    void inject(SimpleHostFragment fragment);
    AppComponent INSTANCE = DaggerAppComponent.create();

    void inject(DaggerFragmentHost host);

    void inject(DaggerFragmentNavHost navHost);
}
