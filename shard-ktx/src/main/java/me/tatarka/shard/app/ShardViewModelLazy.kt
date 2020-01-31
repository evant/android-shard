package me.tatarka.shard.app

import androidx.annotation.MainThread
import androidx.lifecycle.*
import kotlin.reflect.KClass

@MainThread
inline fun <reified VM : ViewModel> Shard.viewModels(
        noinline ownerProducer: () -> ViewModelStoreOwner = { this },
        noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)

@MainThread
fun <VM : ViewModel> Shard.createViewModelLazy(
        viewModelClass: KClass<VM>,
        storeProducer: () -> ViewModelStore,
        factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(viewModelClass, storeProducer, factoryPromise)
}
