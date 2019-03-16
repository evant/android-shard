package me.tatarka.shard.nav;

import org.robolectric.shadows.ShadowLooper;

public abstract class LooperTestHelper {

    private static LooperTestHelper INSTANCE;

    public static LooperTestHelper getInstance() {
        if (INSTANCE == null) {
            try {
                Class.forName("org.robolectric.shadows.ShadowLooper");
                INSTANCE = new RobolectricLooperTestHelper();
            } catch (ClassNotFoundException e) {
                INSTANCE = new AndroidLooperTestHelper();
            }
        }
        return INSTANCE;
    }

    public static void withPausedMainLooper(Runnable runnable) {
        getInstance().doWithPausedMainLooper(runnable);
    }

    public abstract void doWithPausedMainLooper(Runnable runnable);

    static class AndroidLooperTestHelper extends LooperTestHelper {

        @Override
        public void doWithPausedMainLooper(Runnable runnable) {
            // Don't need to pause looper on device.
            runnable.run();
        }
    }

    static class RobolectricLooperTestHelper extends LooperTestHelper {

        @Override
        public void doWithPausedMainLooper(Runnable runnable) {
            ShadowLooper.pauseMainLooper();
            runnable.run();
            ShadowLooper.unPauseMainLooper();
        }
    }
}
