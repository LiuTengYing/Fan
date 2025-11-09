package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;
import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.preference.R$attr;
import com.android.internal.R;
import com.android.internal.jank.InteractionJankMonitor;
import com.android.settingslib.RestrictedPreference;
/* loaded from: classes.dex */
public class SeekBarPreference extends RestrictedPreference implements SeekBar.OnSeekBarChangeListener, View.OnKeyListener {
    private int mAccessibilityRangeInfoType;
    private boolean mContinuousUpdates;
    private int mDefaultProgress;
    private int mHapticFeedbackMode;
    private final InteractionJankMonitor mJankMonitor;
    private int mMax;
    private int mMin;
    private CharSequence mOverrideSeekBarStateDescription;
    private int mProgress;
    private SeekBar mSeekBar;
    private CharSequence mSeekBarContentDescription;
    private CharSequence mSeekBarStateDescription;
    private boolean mShouldBlink;
    private boolean mShouldSetProgress;
    private boolean mTrackingTouch;

    public SeekBarPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mJankMonitor = InteractionJankMonitor.getInstance();
        this.mHapticFeedbackMode = 0;
        this.mDefaultProgress = -1;
        this.mShouldSetProgress = true;
        this.mAccessibilityRangeInfoType = 0;
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ProgressBar, i, i2);
        setMax(obtainStyledAttributes.getInt(2, this.mMax));
        setMin(obtainStyledAttributes.getInt(26, this.mMin));
        obtainStyledAttributes.recycle();
        TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R.styleable.SeekBarPreference, i, i2);
        int resourceId = obtainStyledAttributes2.getResourceId(0, 17367279);
        obtainStyledAttributes2.recycle();
        setSelectable(false);
        setLayoutResource(resourceId);
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, TypedArrayUtils.getAttr(context, R$attr.seekBarPreferenceStyle, 17957081));
    }

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    public void setShouldSetProgress(boolean z) {
        this.mShouldSetProgress = z;
    }

    @Override // androidx.preference.Preference
    public boolean isSelectable() {
        if (isDisabledByAdmin()) {
            return true;
        }
        return super.isSelectable();
    }

    @Override // com.android.settingslib.RestrictedPreference, com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        preferenceViewHolder.itemView.setOnKeyListener(this);
        SeekBar seekBar = (SeekBar) preferenceViewHolder.findViewById(16909465);
        this.mSeekBar = seekBar;
        seekBar.setOnSeekBarChangeListener(this);
        this.mSeekBar.setMax(this.mMax);
        this.mSeekBar.setMin(this.mMin);
        if (this.mShouldSetProgress) {
            this.mSeekBar.setProgress(this.mProgress);
        }
        this.mSeekBar.setEnabled(isEnabled());
        CharSequence title = getTitle();
        if (!TextUtils.isEmpty(this.mSeekBarContentDescription)) {
            this.mSeekBar.setContentDescription(this.mSeekBarContentDescription);
        } else if (!TextUtils.isEmpty(title)) {
            this.mSeekBar.setContentDescription(title);
        }
        if (!TextUtils.isEmpty(this.mSeekBarStateDescription)) {
            this.mSeekBar.setStateDescription(this.mSeekBarStateDescription);
        }
        SeekBar seekBar2 = this.mSeekBar;
        if (seekBar2 instanceof DefaultIndicatorSeekBar) {
            ((DefaultIndicatorSeekBar) seekBar2).setDefaultProgress(this.mDefaultProgress);
        }
        if (this.mShouldBlink) {
            final View view = preferenceViewHolder.itemView;
            view.post(new Runnable() { // from class: com.android.settings.widget.SeekBarPreference$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SeekBarPreference.this.lambda$onBindViewHolder$0(view);
                }
            });
        }
        this.mSeekBar.setAccessibilityDelegate(new View.AccessibilityDelegate() { // from class: com.android.settings.widget.SeekBarPreference.1
            @Override // android.view.View.AccessibilityDelegate
            public void onInitializeAccessibilityNodeInfo(View view2, AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(view2, accessibilityNodeInfo);
                AccessibilityNodeInfo.RangeInfo rangeInfo = accessibilityNodeInfo.getRangeInfo();
                if (rangeInfo != null) {
                    accessibilityNodeInfo.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(SeekBarPreference.this.mAccessibilityRangeInfoType, rangeInfo.getMin(), rangeInfo.getMax(), rangeInfo.getCurrent()));
                }
                if (SeekBarPreference.this.mOverrideSeekBarStateDescription != null) {
                    accessibilityNodeInfo.setStateDescription(SeekBarPreference.this.mOverrideSeekBarStateDescription);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onBindViewHolder$0(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setHotspot(view.getWidth() / 2, view.getHeight() / 2);
        }
        view.setPressed(true);
        view.setPressed(false);
        this.mShouldBlink = false;
    }

    @Override // androidx.preference.Preference
    protected void onSetInitialValue(boolean z, Object obj) {
        int intValue;
        if (z) {
            intValue = getPersistedInt(this.mProgress);
        } else {
            intValue = ((Integer) obj).intValue();
        }
        setProgress(intValue);
    }

    @Override // androidx.preference.Preference
    protected Object onGetDefaultValue(TypedArray typedArray, int i) {
        return Integer.valueOf(typedArray.getInt(i, 0));
    }

    @Override // android.view.View.OnKeyListener
    public boolean onKey(View view, int i, KeyEvent keyEvent) {
        SeekBar seekBar;
        if (keyEvent.getAction() == 0 && (seekBar = (SeekBar) view.findViewById(16909465)) != null) {
            return seekBar.onKeyDown(i, keyEvent);
        }
        return false;
    }

    public void setMax(int i) {
        if (i != this.mMax) {
            this.mMax = i;
            notifyChanged();
        }
    }

    public void setMin(int i) {
        if (i != this.mMin) {
            this.mMin = i;
            notifyChanged();
        }
    }

    public int getMax() {
        return this.mMax;
    }

    public int getMin() {
        return this.mMin;
    }

    public void setProgress(int i) {
        setProgress(i, true);
    }

    public void setContinuousUpdates(boolean z) {
        this.mContinuousUpdates = z;
    }

    public void setHapticFeedbackMode(int i) {
        this.mHapticFeedbackMode = i;
    }

    private void setProgress(int i, boolean z) {
        int i2 = this.mMax;
        if (i > i2) {
            i = i2;
        }
        int i3 = this.mMin;
        if (i < i3) {
            i = i3;
        }
        if (i != this.mProgress) {
            this.mProgress = i;
            persistInt(i);
            if (z) {
                notifyChanged();
            }
        }
    }

    public int getProgress() {
        return this.mProgress;
    }

    void syncProgress(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (progress != this.mProgress) {
            if (callChangeListener(Integer.valueOf(progress))) {
                setProgress(progress, false);
                int i = this.mHapticFeedbackMode;
                if (i == 1) {
                    seekBar.performHapticFeedback(4);
                    return;
                } else if (i != 2) {
                    return;
                } else {
                    if (progress == this.mMax || progress == this.mMin) {
                        seekBar.performHapticFeedback(4);
                        return;
                    }
                    return;
                }
            }
            seekBar.setProgress(this.mProgress);
        }
    }

    public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
        if (z) {
            if (this.mContinuousUpdates || !this.mTrackingTouch) {
                syncProgress(seekBar);
            }
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        this.mTrackingTouch = true;
        this.mJankMonitor.begin(InteractionJankMonitor.Configuration.Builder.withView(53, seekBar).setTag(getKey()));
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        this.mTrackingTouch = false;
        if (seekBar.getProgress() != this.mProgress) {
            syncProgress(seekBar);
        }
        this.mJankMonitor.end(53);
    }

    public void overrideSeekBarStateDescription(CharSequence charSequence) {
        this.mOverrideSeekBarStateDescription = charSequence;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public Parcelable onSaveInstanceState() {
        Parcelable onSaveInstanceState = super.onSaveInstanceState();
        if (isPersistent()) {
            return onSaveInstanceState;
        }
        SavedState savedState = new SavedState(onSaveInstanceState);
        savedState.progress = this.mProgress;
        savedState.max = this.mMax;
        savedState.min = this.mMin;
        return savedState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!parcelable.getClass().equals(SavedState.class)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mProgress = savedState.progress;
        this.mMax = savedState.max;
        this.mMin = savedState.min;
        notifyChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class SavedState extends Preference.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.settings.widget.SeekBarPreference.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int max;
        int min;
        int progress;

        public SavedState(Parcel parcel) {
            super(parcel);
            this.progress = parcel.readInt();
            this.max = parcel.readInt();
            this.min = parcel.readInt();
        }

        @Override // android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.progress);
            parcel.writeInt(this.max);
            parcel.writeInt(this.min);
        }

        public SavedState(Parcelable parcelable) {
            super(parcelable);
        }
    }
}
