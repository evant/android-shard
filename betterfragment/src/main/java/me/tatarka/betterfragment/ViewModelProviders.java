package me.tatarka.betterfragment;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ViewModelProviders {
    private static DefaultFactory defaultFactory;

    private ViewModelProviders() {
    }

    public static ViewModelProvider of(Fragment fragment) {
        initializeFactoryIfNeeded(checkApplicationContext(checkContext(fragment)));
        return new ViewModelProvider(fragment.getViewModelStore(), defaultFactory);
    }

    public static ViewModelProvider of(Fragment fragment, ViewModelProvider.Factory factory) {
        checkApplicationContext(checkContext(fragment));
        return new ViewModelProvider(fragment.getViewModelStore(), factory);
    }

    private static Context checkContext(Fragment fragment) {
        Context context = fragment.getContext();
        if (context == null) {
            throw new IllegalStateException("Can't newInstance ViewModelProvider for detached fragment");
        }
        return context;
    }

    private static Application checkApplicationContext(Context context) {
        Application application = (Application) context.getApplicationContext();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    private static void initializeFactoryIfNeeded(Application application) {
        if (defaultFactory == null) {
            defaultFactory = new DefaultFactory(application);
        }
    }

    /**
     * {@link androidx.lifecycle.ViewModelProvider.Factory} which may newInstance {@link AndroidViewModel} and
     * {@link ViewModel}, which have an empty constructor.
     */
    @SuppressWarnings("WeakerAccess")
    static class DefaultFactory extends ViewModelProvider.NewInstanceFactory {
        private Application mApplication;

        /**
         * Creates a {@code DefaultFactory}
         *
         * @param application an application to pass in {@link AndroidViewModel}
         */
        public DefaultFactory(@NonNull Application application) {
            mApplication = application;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (AndroidViewModel.class.isAssignableFrom(modelClass)) {
                //noinspection TryWithIdenticalCatches
                try {
                    return modelClass.getConstructor(Application.class).newInstance(mApplication);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Cannot newInstance an instance of " + modelClass, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot newInstance an instance of " + modelClass, e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Cannot newInstance an instance of " + modelClass, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Cannot newInstance an instance of " + modelClass, e);
                }
            }
            return super.create(modelClass);
        }
    }
}
