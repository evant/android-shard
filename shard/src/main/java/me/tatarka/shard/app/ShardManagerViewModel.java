package me.tatarka.shard.app;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

class ShardManagerViewModel extends ViewModel {
    private static final ViewModelProvider.Factory FACTORY = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ShardManagerViewModel();
        }
    };

    private static final String KEY = "me.tatarka.shard.VIEW_MODEL";

    static ShardManagerViewModel get(ViewModelStore store) {
        return new ViewModelProvider(store, FACTORY).get(KEY, ShardManagerViewModel.class);
    }

    private final SparseArray<ViewModelStore> viewModelStores = new SparseArray<>();

    int nextId() {
        int size = viewModelStores.size();
        if (size == 0) {
            return 0;
        }
        return viewModelStores.keyAt(size - 1) + 1;
    }

    ViewModelStore get(int id) {
        ViewModelStore store = viewModelStores.get(id);
        if (store == null) {
            store = new ViewModelStore();
            viewModelStores.put(id, store);
        }
        return store;
    }

    void remove(int id) {
        ViewModelStore store = viewModelStores.get(id);
        if (store != null) {
            store.clear();
            viewModelStores.remove(id);
        }
    }

    @Override
    protected void onCleared() {
        for (int i = 0; i < viewModelStores.size(); i++) {
            viewModelStores.valueAt(i).clear();
        }
        viewModelStores.clear();
    }
}
