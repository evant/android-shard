package me.tatarka.shard.app;

import android.content.Intent;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import me.tatarka.shard.activity.ActivityCallbacks;

public abstract class ActivityCallbacksDispatcher implements ActivityCallbacks {

    protected final LifecycleOwner lifecycleOwner;

    private final CopyOnWriteArrayList<OnActivityCallbacks> callbacks = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<OnActivityCallbacksAdapter> adapterCallbacks = new CopyOnWriteArrayList<>();

    protected ActivityCallbacksDispatcher(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    public final void addOnActivityResultCallback(final int requestCode, @NonNull final OnActivityResultCallback listener) {
        adapterCallbacks.add(new OnActivityCallbacksAdapter(listener) {
            @Override
            public boolean onActivityResult(int r, int resultCode, @Nullable Intent data) {
                if (r == requestCode) {
                    listener.onActivityResult(resultCode, data);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public final void removeActivityResultCallback(@NonNull OnActivityResultCallback callback) {
        removeCallback(callback);
    }

    @Override
    public final void addOnRequestPermissionResultCallback(final int requestCode, @NonNull final OnRequestPermissionResultCallback listener) {
        adapterCallbacks.add(new OnActivityCallbacksAdapter(listener) {
            @Override
            public boolean onRequestPermissionResult(int r, @NonNull String[] permissions, @NonNull int[] grantResults) {
                if (r == requestCode) {
                    listener.onRequestPermissionResult(permissions, grantResults);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public final void removeOnRequestPermissionResultCallback(@NonNull OnRequestPermissionResultCallback callback) {
        removeCallback(callback);
    }

    @Override
    public final void addOnBackPressedCallback(OnBackPressedCallback callback) {
        addOnBackPressedCallback(lifecycleOwner, callback);
    }

    @Override
    public final void addOnMultiWindowModeChangedCallback(@NonNull final OnMultiWindowModeChangedCallback listener) {
        adapterCallbacks.add(new OnActivityCallbacksAdapter(listener) {
            @Override
            public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
                listener.onMultiWindowModeChanged(isInMultiWindowMode);
            }
        });
    }

    @Override
    public final void removeOnMultiWindowModeChangedCallback(@NonNull OnMultiWindowModeChangedCallback listener) {
        removeCallback(listener);
    }

    @Override
    public final void addOnPictureInPictureModeChangedCallback(@NonNull final OnPictureInPictureModeChangedCallback listener) {
        adapterCallbacks.add(new OnActivityCallbacksAdapter(listener) {
            @Override
            public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
                listener.onPictureInPictureModeChanged(isInPictureInPictureMode);
            }
        });
    }

    @Override
    public final void removeOnPictureInPictureModeChangedCallback(@NonNull OnPictureInPictureModeChangedCallback listener) {
        removeCallback(listener);
    }

    @Override
    public final void addOnActivityCallbacks(OnActivityCallbacks callbacks) {
        this.callbacks.add(callbacks);
    }

    @Override
    public final void removeOnActivityCallbacks(OnActivityCallbacks callbacks) {
        this.callbacks.remove(callbacks);
    }

    public final void dispatchOnActivityResult(int requestCode, int resultCode, Intent data) {
        for (OnActivityCallbacks callback : callbacks) {
            if (callback.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
        for (OnActivityCallbacksAdapter adapterCallback : adapterCallbacks) {
            if (adapterCallback.onActivityResult(requestCode, resultCode, data)) {
                return;
            }
        }
    }

    public final void dispatchOnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (OnActivityCallbacks callback : callbacks) {
            if (callback.onRequestPermissionResult(requestCode, permissions, grantResults)) {
                return;
            }
        }
        for (OnActivityCallbacksAdapter adapterCallback : adapterCallbacks) {
            if (adapterCallback.onRequestPermissionResult(requestCode, permissions, grantResults)) {
                return;
            }
        }
    }

    public final void dispatchOnMultiWindowModeChanged(boolean isInMultiWindowMode) {
        for (OnActivityCallbacks callback : callbacks) {
            callback.onMultiWindowModeChanged(isInMultiWindowMode);
        }
        for (OnActivityCallbacksAdapter adapterCallback : adapterCallbacks) {
            adapterCallback.onMultiWindowModeChanged(isInMultiWindowMode);
        }
    }

    public final void dispatchOnPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        for (OnActivityCallbacks callback : callbacks) {
            callback.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
        for (OnActivityCallbacksAdapter adapterCallback : adapterCallbacks) {
            adapterCallback.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
    }

    private void removeCallback(Object delegate) {
        Iterator<OnActivityCallbacksAdapter> itr = adapterCallbacks.iterator();
        while (itr.hasNext()) {
            OnActivityCallbacksAdapter c = itr.next();
            if (c.delegate == delegate) {
                itr.remove();
                return;
            }
        }
    }

    static abstract class OnActivityCallbacksAdapter implements OnActivityCallbacks {
        final Object delegate;

        OnActivityCallbacksAdapter(Object delegate) {
            this.delegate = delegate;
        }

        @Override
        public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {

        }

        @Override
        public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {

        }

        @Override
        public boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            return false;
        }

        @Override
        public boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            return false;
        }
    }
}
