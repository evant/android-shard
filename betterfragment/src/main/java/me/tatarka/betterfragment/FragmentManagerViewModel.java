package me.tatarka.betterfragment;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

class FragmentManagerViewModel extends ViewModel {
    private static final ViewModelProvider.Factory FACTORY = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new FragmentManagerViewModel();
        }
    };

    private static final String KEY = "me.tatarka.betterfragment.VIEW_MODEL";

    static FragmentManagerViewModel get(ViewModelStore store) {
        return new ViewModelProvider(store, FACTORY).get(KEY, FragmentManagerViewModel.class);
    }

    private final SparseArray<ViewModelStore> viewModelStores = new SparseArray<>();

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
