package me.tatarka.shard.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

public interface ActivityCallbacks {

    /**
     * Same as calling {@link #startActivityForResult(Intent, int, Bundle)}
     * with no options.
     *
     * @param intent      The intent to start.
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     * @throws android.content.ActivityNotFoundException
     * @see android.app.Activity#startActivity
     */
    void startActivityForResult(@NonNull Intent intent, int requestCode);

    /**
     * Launch an activity for which you would like a result when it finished.
     * When this activity exits, your
     * onActivityResult() method will be called with the given requestCode.
     * Using a negative requestCode is the same as calling
     * {@link android.app.Activity#startActivity} (the activity is not launched as a sub-activity).
     *
     * <p>Note that this method should only be used with Intent protocols
     * that are defined to return a result.  In other protocols (such as
     * {@link Intent#ACTION_MAIN} or {@link Intent#ACTION_VIEW}), you may
     * not get the result when you expect.  For example, if the activity you
     * are launching uses {@link Intent#FLAG_ACTIVITY_NEW_TASK}, it will not
     * run in your task and thus you will immediately receive a cancel result.
     *
     * <p>As a special case, if you call startActivityForResult() with a requestCode
     * >= 0 during the initial onCreate(Bundle savedInstanceState)/onResume() of your
     * activity, then your window will not be displayed until a result is
     * returned back from the started activity.  This is to avoid visible
     * flickering when redirecting to another activity.
     *
     * <p>This method throws {@link android.content.ActivityNotFoundException}
     * if there was no Activity found to run the given Intent.
     *
     * @param intent      The intent to start.
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     * @param options     Additional options for how the Activity should be started.
     *                    See {@link android.content.Context#startActivity(Intent, Bundle)}
     *                    Context.startActivity(Intent, Bundle)} for more details.
     * @throws android.content.ActivityNotFoundException
     * @see android.app.Activity#startActivity
     */
    void startActivityForResult(@NonNull Intent intent, int requestCode, @Nullable Bundle options);

    /**
     * Same as calling {@link #startIntentSenderForResult(IntentSender, int,
     * Intent, int, int, int, Bundle)} with no options.
     *
     * @param intent The IntentSender to launch.
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     * @param fillInIntent If non-null, this will be provided as the
     * intent parameter to {@link IntentSender#sendIntent}.
     * @param flagsMask Intent flags in the original IntentSender that you
     * would like to change.
     * @param flagsValues Desired values for any bits set in
     * <var>flagsMask</var>
     * @param extraFlags Always set to 0.
     */
    void startIntentSenderForResult(IntentSender intent, int requestCode,
                                           @Nullable Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags)
            throws IntentSender.SendIntentException;

    /**
     * Like {@link #startActivityForResult(Intent, int)}, but allowing you
     * to use a IntentSender to describe the activity to be started.  If
     * the IntentSender is for an activity, that activity will be started
     * as if you had called the regular {@link #startActivityForResult(Intent, int)}
     * here; otherwise, its associated action will be executed (such as
     * sending a broadcast) as if you had called
     * {@link IntentSender#sendIntent IntentSender.sendIntent} on it.
     *
     * @param intent The IntentSender to launch.
     * @param requestCode If >= 0, this code will be returned in
     *                    onActivityResult() when the activity exits.
     * @param fillInIntent If non-null, this will be provided as the
     * intent parameter to {@link IntentSender#sendIntent}.
     * @param flagsMask Intent flags in the original IntentSender that you
     * would like to change.
     * @param flagsValues Desired values for any bits set in
     * <var>flagsMask</var>
     * @param extraFlags Always set to 0.
     * @param options Additional options for how the Activity should be started.
     * See {@link android.content.Context#startActivity(Intent, Bundle)}
     * Context.startActivity(Intent, Bundle)} for more details.  If options
     * have also been supplied by the IntentSender, options given here will
     * override any that conflict with those given by the IntentSender.
     */
    void startIntentSenderForResult(@NonNull IntentSender intent, int requestCode,
                                    @Nullable Intent fillIntent, int flagsMask, int flagsValues, int extraFlags,
                                    Bundle options) throws IntentSender.SendIntentException;

    interface OnActivityResultCallback {
        /**
         * Called when an activity you launched exits, giving you the requestCode
         * you started it with, the resultCode it returned, and any additional
         * data from it.  The <var>resultCode</var> will be
         * {@link android.app.Activity#RESULT_CANCELED} if the activity explicitly returned that,
         * didn't return any result, or crashed during its operation.
         *
         * <p>You will receive this call immediately before onResume() when your
         * activity is re-starting.
         *
         * <p>This method is never invoked if your activity sets
         * {@link android.R.styleable#AndroidManifestActivity_noHistory noHistory} to
         * <code>true</code>.
         *
         * @param resultCode The integer result code returned by the child activity
         *                   through its setResult().
         * @param data       An Intent, which can return result data to the caller
         *                   (various data can be attached to Intent "extras").
         * @see android.app.Activity#startActivityForResult
         * @see android.app.Activity#createPendingResult
         * @see android.app.Activity#setResult(int)
         */
        void onActivityResult(int resultCode, @Nullable Intent data);
    }

    void addOnActivityResultCallback(int requestCode, @NonNull OnActivityResultCallback onActivityResultCallback);

    void removeActivityResultCallback(@NonNull OnActivityResultCallback callback);

    /**
     * Requests permissions to be granted to this application. These permissions
     * must be requested in your manifest, they should not be granted to your app,
     * and they should have protection level {@link android.content.pm.PermissionInfo
     * #PROTECTION_DANGEROUS dangerous}, regardless whether they are declared by
     * the platform or a third-party app.
     * <p>
     * Normal permissions {@link android.content.pm.PermissionInfo#PROTECTION_NORMAL}
     * are granted at install time if requested in the manifest. Signature permissions
     * {@link android.content.pm.PermissionInfo#PROTECTION_SIGNATURE} are granted at
     * install time if requested in the manifest and the signature of your app matches
     * the signature of the app declaring the permissions.
     * </p>
     * <p>
     * If your app does not have the requested permissions the user will be presented
     * with UI for accepting them. After the user has accepted or rejected the
     * requested permissions you will receive a callback on {@link
     * android.app.Activity#onRequestPermissionsResult(int, String[], int[])} reporting whether the
     * permissions were granted or not.
     * </p>
     * <p>
     * Note that requesting a permission does not guarantee it will be granted and
     * your app should be able to run without having this permission.
     * </p>
     * <p>
     * This method may start an activity allowing the user to choose which permissions
     * to grant and which to reject. Hence, you should be prepared that your activity
     * may be paused and resumed. Further, granting some permissions may require
     * a restart of you application. In such a case, the system will recreate the
     * activity stack before delivering the result to {@link
     * android.app.Activity#onRequestPermissionsResult(int, String[], int[])}.
     * </p>
     * <p>
     * When checking whether you have a permission you should use {@link
     * android.app.Activity#checkSelfPermission(String)}.
     * </p>
     * <p>
     * Calling this API for permissions already granted to your app would show UI
     * to the user to decide whether the app can still hold these permissions. This
     * can be useful if the way your app uses data guarded by the permissions
     * changes significantly.
     * </p>
     * <p>
     * You cannot request a permission if your activity sets {@link
     * android.R.styleable#AndroidManifestActivity_noHistory noHistory} to
     * <code>true</code> because in this case the activity would not receive
     * result callbacks including {@link android.app.Activity#onRequestPermissionsResult(int, String[], int[])}.
     * </p>
     * <p>
     * The <a href="http://developer.android.com/samples/RuntimePermissions/index.html">
     * RuntimePermissions</a> sample app demonstrates how to use this method to
     * request permissions at run time.
     * </p>
     *
     * @param permissions The requested permissions. Must me non-null and not empty.
     * @param requestCode Application specific request code to match with a result
     *                    reported to {@link android.app.Activity#onRequestPermissionsResult(int, String[], int[])}.
     *                    Should be >= 0.
     * @throws IllegalArgumentException if requestCode is negative.
     * @see android.app.Activity#onRequestPermissionsResult(int, String[], int[])
     * @see android.app.Activity#checkSelfPermission(String)
     * @see android.app.Activity#shouldShowRequestPermissionRationale(String)
     */
    void requestPermissions(@NonNull String[] permissions, int requestCode);

    /**
     * Gets whether you should show UI with rationale for requesting a permission.
     * You should do this only if you do not have the permission and the context in
     * which the permission is requested does not clearly communicate to the user
     * what would be the benefit from granting this permission.
     * <p>
     * For example, if you write a camera app, requesting the camera permission
     * would be expected by the user and no rationale for why it is requested is
     * needed. If however, the app needs location for tagging photos then a non-tech
     * savvy user may wonder how location is related to taking photos. In this case
     * you may choose to show UI with rationale of requesting this permission.
     * </p>
     *
     * @param permission A permission your app wants to request.
     * @return Whether you can show permission rationale UI.
     * @see android.content.Context#checkSelfPermission(String)
     * @see #requestPermissions(String[], int)
     * @see OnRequestPermissionResultCallback#onRequestPermissionResult(String[], int[])
     */
    boolean shouldShowRequestPermissionRationale(@NonNull String permission);

    interface OnRequestPermissionResultCallback {

        /**
         * Callback for the result from requesting permissions. This method
         * is invoked for every call on {@link #requestPermissions(String[], int)}.
         * <p>
         * <strong>Note:</strong> It is possible that the permissions request interaction
         * with the user is interrupted. In this case you will receive empty permissions
         * and results arrays which should be treated as a cancellation.
         * </p>
         *
         * @param permissions  The requested permissions. Never null.
         * @param grantResults The grant results for the corresponding permissions
         *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
         *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
         * @see #requestPermissions(String[], int)
         */
        void onRequestPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults);
    }

    void addOnRequestPermissionResultCallback(int requestCode, @NonNull OnRequestPermissionResultCallback onRequestPermissionResultCallback);

    void removeOnRequestPermissionResultCallback(@NonNull OnRequestPermissionResultCallback onRequestPermissionResultCallback);

    boolean isInMultiWindowMode();

    interface OnMultiWindowModeChangedCallback {
        /**
         * Called when the Fragment's activity changes from fullscreen mode to multi-window mode and
         * visa-versa. This is generally tied to {@link android.app.Activity#onMultiWindowModeChanged} of the
         * containing Activity.
         *
         * @param isInMultiWindowMode True if the activity is in multi-window mode.
         */
        void onMultiWindowModeChanged(boolean isInMultiWindowMode);
    }

    void addOnMultiWindowModeChangedCallback(@NonNull OnMultiWindowModeChangedCallback onMultiWindowModeChangedCallback);

    void removeOnMultiWindowModeChangedCallback(@NonNull OnMultiWindowModeChangedCallback onMultiWindowModeChangedCallback);

    boolean isInPictureInPictureMode();

    interface OnPictureInPictureModeChangedCallback {
        /**
         * Called by the system when the activity changes to and from picture-in-picture mode. This is
         * generally tied to {@link android.app.Activity#onPictureInPictureModeChanged} of the containing Activity.
         *
         * @param isInPictureInPictureMode True if the activity is in picture-in-picture mode.
         */
        void onPictureInPictureModeChanged(boolean isInPictureInPictureMode);
    }

    void addOnPictureInPictureModeChangedCallback(@NonNull OnPictureInPictureModeChangedCallback onPictureInPictureModeChangedCallback);

    void removeOnPictureInPictureModeChangedCallback(@NonNull OnPictureInPictureModeChangedCallback onPictureInPictureModeChangedCallback);

    /**
     * Add a new {@link OnBackPressedCallback}. Callbacks are invoked in order of recency, so
     * this newly added {@link OnBackPressedCallback} will be the first callback to receive a
     * callback if {@link Activity#onBackPressed()} is called. Only if this callback returns
     * <code>false</code> from its {@link OnBackPressedCallback#handleOnBackPressed()} will any
     * previously added callback be called.
     *
     * @param onBackPressedCallback The callback to add
     * @see Activity#onBackPressed()
     * @see #removeOnBackPressedCallback(OnBackPressedCallback)
     */
    void addOnBackPressedCallback(OnBackPressedCallback onBackPressedCallback);

    void addOnBackPressedCallback(LifecycleOwner owner, OnBackPressedCallback onBackPressedCallback);

    /**
     * Remove a previously
     * {@link #addOnBackPressedCallback(OnBackPressedCallback)} added}
     * {@link OnBackPressedCallback} instance. The callback won't be called for any future
     * {@link Activity#onBackPressed()} calls, but may still receive a callback if this method is called
     * during the dispatch of an ongoing {@link Activity#onBackPressed()} call.
     * <p>
     * This call is usually not necessary as callbacks will be automatically removed when their
     * associated {@link LifecycleOwner} is {@link Lifecycle.State#DESTROYED destroyed}.
     *
     * @param onBackPressedCallback The callback to remove
     */
    void removeOnBackPressedCallback(OnBackPressedCallback onBackPressedCallback);

    interface OnActivityCallbacks extends OnMultiWindowModeChangedCallback, OnPictureInPictureModeChangedCallback {

        /**
         * Called when an activity you launched exits, giving you the requestCode
         * you started it with, the resultCode it returned, and any additional
         * data from it.  The <var>resultCode</var> will be
         * {@link android.app.Activity#RESULT_CANCELED} if the activity explicitly returned that,
         * didn't return any result, or crashed during its operation.
         *
         * <p>You will receive this call immediately before onResume() when your
         * activity is re-starting.
         *
         * <p>This method is never invoked if your activity sets
         * {@link android.R.styleable#AndroidManifestActivity_noHistory noHistory} to
         * <code>true</code>.
         *
         * @param requestCode The integer request code originally supplied to
         *                    startActivityForResult(), allowing you to identify who this
         *                    result came from.
         * @param resultCode The integer result code returned by the child activity
         *                   through its setResult().
         * @param data       An Intent, which can return result data to the caller
         *                   (various data can be attached to Intent "extras").
         * @see android.app.Activity#startActivityForResult
         * @see android.app.Activity#createPendingResult
         * @see android.app.Activity#setResult(int)
         */
        boolean onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

        /**
         * Callback for the result from requesting permissions. This method
         * is invoked for every call on {@link #requestPermissions(String[], int)}.
         * <p>
         * <strong>Note:</strong> It is possible that the permissions request interaction
         * with the user is interrupted. In this case you will receive empty permissions
         * and results arrays which should be treated as a cancellation.
         * </p>
         *
         * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
         * @param permissions The requested permissions. Never null.
         * @param grantResults The grant results for the corresponding permissions
         *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
         *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
         *
         * @see #requestPermissions(String[], int)
         */
        boolean onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }

    void addOnActivityCallbacks(OnActivityCallbacks callbacks);

    void removeOnActivityCallbacks(OnActivityCallbacks callbacks);
}
