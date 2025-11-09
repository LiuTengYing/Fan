package com.android.settings.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceGroupAdapter;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceViewHolder;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$color;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.SettingsPreferenceFragment;
import com.google.android.material.appbar.AppBarLayout;
/* loaded from: classes.dex */
public class HighlightablePreferenceGroupAdapter extends PreferenceGroupAdapter {
    static final long DELAY_COLLAPSE_DURATION_MILLIS = 300;
    static final long DELAY_HIGHLIGHT_DURATION_MILLIS = 600;
    private static final int RES_NORMAL_BACKGROUND = R$drawable.highlighted_item_background;
    boolean mFadeInAnimated;
    final int mHighlightColor;
    private final String mHighlightKey;
    private int mHighlightPosition;
    private boolean mHighlightRequested;
    private final int mNormalBackgroundRes;

    public static void adjustInitialExpandedChildCount(SettingsPreferenceFragment settingsPreferenceFragment) {
        PreferenceScreen preferenceScreen;
        if (settingsPreferenceFragment == null || (preferenceScreen = settingsPreferenceFragment.getPreferenceScreen()) == null) {
            return;
        }
        Bundle arguments = settingsPreferenceFragment.getArguments();
        if (arguments != null && !TextUtils.isEmpty(arguments.getString(":settings:fragment_args_key"))) {
            preferenceScreen.setInitialExpandedChildrenCount(Integer.MAX_VALUE);
            return;
        }
        int initialExpandedChildCount = settingsPreferenceFragment.getInitialExpandedChildCount();
        if (initialExpandedChildCount <= 0) {
            return;
        }
        preferenceScreen.setInitialExpandedChildrenCount(initialExpandedChildCount);
    }

    public HighlightablePreferenceGroupAdapter(PreferenceGroup preferenceGroup, String str, boolean z) {
        super(preferenceGroup);
        this.mHighlightPosition = -1;
        this.mHighlightKey = str;
        this.mHighlightRequested = z;
        Context context = preferenceGroup.getContext();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843534, typedValue, true);
        this.mNormalBackgroundRes = typedValue.resourceId;
        this.mHighlightColor = context.getColor(R$color.preference_highlight_color);
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.preference.PreferenceGroupAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder, int i) {
        super.onBindViewHolder(preferenceViewHolder, i);
        updateBackground(preferenceViewHolder, i);
    }

    void updateBackground(PreferenceViewHolder preferenceViewHolder, int i) {
        String str;
        View view = preferenceViewHolder.itemView;
        if (i == this.mHighlightPosition && (str = this.mHighlightKey) != null && TextUtils.equals(str, getItem(i).getKey())) {
            addHighlightBackground(preferenceViewHolder, !this.mFadeInAnimated);
        } else if (Boolean.TRUE.equals(view.getTag(R$id.preference_highlighted))) {
            removeHighlightBackground(preferenceViewHolder, false);
        }
    }

    public void requestHighlight(View view, final RecyclerView recyclerView, final AppBarLayout appBarLayout) {
        final int preferenceAdapterPosition;
        if (this.mHighlightRequested || recyclerView == null || TextUtils.isEmpty(this.mHighlightKey) || (preferenceAdapterPosition = getPreferenceAdapterPosition(this.mHighlightKey)) < 0) {
            return;
        }
        if (appBarLayout != null) {
            view.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AppBarLayout.this.setExpanded(false, true);
                }
            }, DELAY_COLLAPSE_DURATION_MILLIS);
        }
        view.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                HighlightablePreferenceGroupAdapter.this.lambda$requestHighlight$1(recyclerView, preferenceAdapterPosition);
            }
        }, DELAY_HIGHLIGHT_DURATION_MILLIS);
        view.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                HighlightablePreferenceGroupAdapter.this.lambda$requestHighlight$2(recyclerView, preferenceAdapterPosition);
            }
        }, 900L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHighlight$1(RecyclerView recyclerView, int i) {
        this.mHighlightRequested = true;
        recyclerView.setItemAnimator(null);
        recyclerView.smoothScrollToPosition(i);
        this.mHighlightPosition = i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestHighlight$2(RecyclerView recyclerView, int i) {
        if (recyclerView.isComputingLayout()) {
            return;
        }
        notifyItemChanged(i);
    }

    public boolean isHighlightRequested() {
        return this.mHighlightRequested;
    }

    void requestRemoveHighlightDelayed(final PreferenceViewHolder preferenceViewHolder) {
        preferenceViewHolder.itemView.postDelayed(new Runnable() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                HighlightablePreferenceGroupAdapter.this.lambda$requestRemoveHighlightDelayed$3(preferenceViewHolder);
            }
        }, 15000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestRemoveHighlightDelayed$3(PreferenceViewHolder preferenceViewHolder) {
        this.mHighlightPosition = -1;
        removeHighlightBackground(preferenceViewHolder, true);
    }

    private void addHighlightBackground(PreferenceViewHolder preferenceViewHolder, boolean z) {
        final View view = preferenceViewHolder.itemView;
        view.setTag(R$id.preference_highlighted, Boolean.TRUE);
        if (!z) {
            view.setBackgroundResource(RES_NORMAL_BACKGROUND);
            Log.d("HighlightableAdapter", "AddHighlight: Not animation requested - setting highlight background");
            requestRemoveHighlightDelayed(preferenceViewHolder);
            return;
        }
        this.mFadeInAnimated = true;
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(this.mNormalBackgroundRes), Integer.valueOf(this.mHighlightColor));
        ofObject.setDuration(500L);
        ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                HighlightablePreferenceGroupAdapter.lambda$addHighlightBackground$4(view, valueAnimator);
            }
        });
        ofObject.setRepeatMode(2);
        ofObject.setRepeatCount(4);
        ofObject.start();
        Log.d("HighlightableAdapter", "AddHighlight: starting fade in animation");
        preferenceViewHolder.setIsRecyclable(false);
        requestRemoveHighlightDelayed(preferenceViewHolder);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$addHighlightBackground$4(View view, ValueAnimator valueAnimator) {
        view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    private void removeHighlightBackground(final PreferenceViewHolder preferenceViewHolder, boolean z) {
        final View view = preferenceViewHolder.itemView;
        if (!z) {
            view.setTag(R$id.preference_highlighted, Boolean.FALSE);
            view.setBackgroundResource(this.mNormalBackgroundRes);
            Log.d("HighlightableAdapter", "RemoveHighlight: No animation requested - setting normal background");
            return;
        }
        Boolean bool = Boolean.TRUE;
        int i = R$id.preference_highlighted;
        if (!bool.equals(view.getTag(i))) {
            Log.d("HighlightableAdapter", "RemoveHighlight: Not highlighted - skipping");
            return;
        }
        int i2 = R$drawable.homepage_highlighted_item_background;
        view.setTag(i, Boolean.FALSE);
        ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(i2), Integer.valueOf(i2));
        ofObject.setDuration(500L);
        ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                HighlightablePreferenceGroupAdapter.lambda$removeHighlightBackground$5(view, valueAnimator);
            }
        });
        ofObject.addListener(new AnimatorListenerAdapter() { // from class: com.android.settings.widget.HighlightablePreferenceGroupAdapter.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setBackgroundResource(HighlightablePreferenceGroupAdapter.RES_NORMAL_BACKGROUND);
                preferenceViewHolder.setIsRecyclable(true);
            }
        });
        ofObject.start();
        Log.d("HighlightableAdapter", "Starting fade out animation");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$removeHighlightBackground$5(View view, ValueAnimator valueAnimator) {
        view.setBackgroundColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }
}
