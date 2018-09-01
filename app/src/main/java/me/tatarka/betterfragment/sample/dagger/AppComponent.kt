package me.tatarka.betterfragment.sample.dagger

import javax.inject.Singleton

import dagger.Component
import me.tatarka.betterfragment.sample.MainActivity
import me.tatarka.betterfragment.sample.SimpleHostFragment

@Singleton
@Component(modules = [FragmentModule::class])
interface AppComponent {
    val fragmentFactory: DaggerFragmentFactory
}

val injector: AppComponent = DaggerAppComponent.create()
