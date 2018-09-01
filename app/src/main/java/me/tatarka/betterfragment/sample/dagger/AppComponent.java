package me.tatarka.betterfragment.sample.dagger;

import javax.inject.Singleton;

import dagger.Component;
import me.tatarka.betterfragment.sample.MainActivity;
import me.tatarka.betterfragment.sample.SimpleHostFragment;

@Singleton
@Component(modules = FragmentModule.class)
public interface AppComponent {
    AppComponent INSTANCE = DaggerAppComponent.create();

    void inject(SimpleHostFragment fragment);

    void inject(DaggerFragmentHost host);

    void inject(DaggerFragmentNavHost navHost);

    void inject(MainActivity activity);
}
