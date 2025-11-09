package com.android.settings.deviceinfo;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$layout;
import com.android.settings.deviceinfo.storage.StorageUtils;
/* loaded from: classes.dex */
public class StorageItemPreference extends Preference {
    private ProgressBar mProgressBar;
    private int mProgressPercent;
    private long mStorageSize;

    public StorageItemPreference(Context context) {
        this(context, null);
    }

    public StorageItemPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mProgressPercent = -1;
        setLayoutResource(R$layout.storage_item);
    }

    public void setStorageSize(long j, final long j2, boolean z) {
        if (z) {
            ValueAnimator ofObject = ValueAnimator.ofObject(new TypeEvaluator() { // from class: com.android.settings.deviceinfo.StorageItemPreference$$ExternalSyntheticLambda0
                @Override // android.animation.TypeEvaluator
                public final Object evaluate(float f, Object obj, Object obj2) {
                    Long lambda$setStorageSize$0;
                    lambda$setStorageSize$0 = StorageItemPreference.lambda$setStorageSize$0(f, (Long) obj, (Long) obj2);
                    return lambda$setStorageSize$0;
                }
            }, Long.valueOf(this.mStorageSize), Long.valueOf(j));
            ofObject.setDuration(1000L);
            ofObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.settings.deviceinfo.StorageItemPreference$$ExternalSyntheticLambda1
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    StorageItemPreference.this.lambda$setStorageSize$1(j2, valueAnimator);
                }
            });
            ofObject.start();
        } else {
            updateProgressBarAndSizeInfo(j, j2);
        }
        this.mStorageSize = j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ Long lambda$setStorageSize$0(float f, Long l, Long l2) {
        return (f < 1.0f || l2.longValue() != 0) ? Long.valueOf(l.longValue() + (f * ((float) (l2.longValue() - l.longValue())))) : l2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setStorageSize$1(long j, ValueAnimator valueAnimator) {
        updateProgressBarAndSizeInfo(((Long) valueAnimator.getAnimatedValue()).longValue(), j);
    }

    public long getStorageSize() {
        return this.mStorageSize;
    }

    protected void updateProgressBar() {
        ProgressBar progressBar = this.mProgressBar;
        if (progressBar == null || this.mProgressPercent == -1) {
            return;
        }
        progressBar.setMax(100);
        this.mProgressBar.setProgress(this.mProgressPercent);
    }

    private void updateProgressBarAndSizeInfo(long j, long j2) {
        setSummary(StorageUtils.getStorageSizeLabel(getContext(), j));
        this.mProgressPercent = j2 == 0 ? 0 : (int) ((j * 100) / j2);
        updateProgressBar();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        this.mProgressBar = (ProgressBar) preferenceViewHolder.findViewById(16908301);
        updateProgressBar();
        super.onBindViewHolder(preferenceViewHolder);
    }
}
