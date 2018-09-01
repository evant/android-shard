package me.tatarka.betterfragment.sample.dagger

import dagger.MapKey
import me.tatarka.betterfragment.app.Fragment
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.reflect.KClass

@MapKey
@Retention(RUNTIME)
annotation class FragmentKey(val value: KClass<out Fragment>)