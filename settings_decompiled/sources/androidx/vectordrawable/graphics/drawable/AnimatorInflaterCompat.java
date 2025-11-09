package androidx.vectordrawable.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Resources;
/* loaded from: classes.dex */
public class AnimatorInflaterCompat {
    public static Animator loadAnimator(Context context, int i) throws Resources.NotFoundException {
        return AnimatorInflater.loadAnimator(context, i);
    }
}
