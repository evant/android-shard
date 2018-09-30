package me.tatarka.shard.transition;

import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Resources;
import android.view.animation.AnimationUtils;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.Nullable;

class AnimUtil {

    @Nullable
    static Object loadAnim(Context context, @AnimatorRes @AnimRes int anim) {
        if (anim == 0 || anim == -1) {
            return null;
        }
        String dir = context.getResources().getResourceTypeName(anim);
        boolean isAnim = "anim".equals(dir);
        if (isAnim) {
            // try AnimationUtils first
            try {
                return AnimationUtils.loadAnimation(context, anim);
            } catch (Resources.NotFoundException e) {
                throw e; // Rethrow it -- the resource should be found if it is provided.
            } catch (RuntimeException e) {
                // Other exceptions can occur when loading an Animator from AnimationUtils.
            }
        }
        // try Animator
        try {
            return AnimatorInflater.loadAnimator(context, anim);
        } catch (RuntimeException e) {
            if (isAnim) {
                // Rethrow it -- we already tried AnimationUtils and it failed.
                throw e;
            }
            // Otherwise, it is probably an animation resource
            return AnimationUtils.loadAnimation(context, anim);
        }
    }
}
