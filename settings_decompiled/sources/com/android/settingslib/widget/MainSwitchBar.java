package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import com.android.settingslib.utils.BuildCompatUtils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class MainSwitchBar extends LinearLayout implements CompoundButton.OnCheckedChangeListener {
    private int mBackgroundActivatedColor;
    private int mBackgroundColor;
    private Drawable mBackgroundDisabled;
    private Drawable mBackgroundOff;
    private Drawable mBackgroundOn;
    private View mFrameView;
    protected Switch mSwitch;
    private final List<OnMainSwitchChangeListener> mSwitchChangeListeners;
    protected TextView mTextView;

    public MainSwitchBar(Context context) {
        this(context, null);
    }

    public MainSwitchBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MainSwitchBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public MainSwitchBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mSwitchChangeListeners = new ArrayList();
        LayoutInflater.from(context).inflate(R$layout.settingslib_main_switch_bar, this);
        setFocusable(true);
        setClickable(true);
        this.mFrameView = findViewById(R$id.frame);
        this.mTextView = (TextView) findViewById(R$id.switch_text);
        this.mSwitch = (Switch) findViewById(16908352);
        if (BuildCompatUtils.isAtLeastS()) {
            Context context2 = getContext();
            int i3 = R$drawable.split_page_item_bk_white;
            this.mBackgroundOn = context2.getDrawable(i3);
            this.mBackgroundOff = getContext().getDrawable(i3);
            this.mBackgroundDisabled = getContext().getDrawable(R$drawable.split_page_item_bk);
        }
        addOnSwitchChangeListener(new OnMainSwitchChangeListener() { // from class: com.android.settingslib.widget.MainSwitchBar$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.widget.OnMainSwitchChangeListener
            public final void onSwitchChanged(Switch r1, boolean z) {
                MainSwitchBar.this.lambda$new$0(r1, z);
            }
        });
        if (this.mSwitch.getVisibility() == 0) {
            this.mSwitch.setOnCheckedChangeListener(this);
        }
        setChecked(this.mSwitch.isChecked());
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, androidx.preference.R$styleable.Preference, 0, 0);
            setTitle(obtainStyledAttributes.getText(androidx.preference.R$styleable.Preference_android_title));
            obtainStyledAttributes.recycle();
        }
        setBackground(this.mSwitch.isChecked());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(Switch r1, boolean z) {
        setChecked(z);
    }

    @Override // android.widget.CompoundButton.OnCheckedChangeListener
    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        propagateChecked(z);
    }

    @Override // android.view.View
    public boolean performClick() {
        this.mSwitch.performClick();
        return super.performClick();
    }

    public void setChecked(boolean z) {
        Switch r0 = this.mSwitch;
        if (r0 != null) {
            r0.setChecked(z);
        }
        setBackground(z);
    }

    public boolean isChecked() {
        return this.mSwitch.isChecked();
    }

    public final Switch getSwitch() {
        return this.mSwitch;
    }

    public void setTitle(CharSequence charSequence) {
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    public void show() {
        setVisibility(0);
        this.mSwitch.setOnCheckedChangeListener(this);
    }

    public void hide() {
        if (isShowing()) {
            setVisibility(8);
            this.mSwitch.setOnCheckedChangeListener(null);
        }
    }

    public boolean isShowing() {
        return getVisibility() == 0;
    }

    public void addOnSwitchChangeListener(OnMainSwitchChangeListener onMainSwitchChangeListener) {
        if (this.mSwitchChangeListeners.contains(onMainSwitchChangeListener)) {
            return;
        }
        this.mSwitchChangeListeners.add(onMainSwitchChangeListener);
    }

    public void removeOnSwitchChangeListener(OnMainSwitchChangeListener onMainSwitchChangeListener) {
        this.mSwitchChangeListeners.remove(onMainSwitchChangeListener);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mTextView.setEnabled(z);
        this.mSwitch.setEnabled(z);
        if (BuildCompatUtils.isAtLeastS()) {
            if (z) {
                this.mFrameView.setBackground(isChecked() ? this.mBackgroundOn : this.mBackgroundOff);
            } else {
                this.mFrameView.setBackground(this.mBackgroundDisabled);
            }
        }
    }

    private void propagateChecked(boolean z) {
        setBackground(z);
        int size = this.mSwitchChangeListeners.size();
        for (int i = 0; i < size; i++) {
            this.mSwitchChangeListeners.get(i).onSwitchChanged(this.mSwitch, z);
        }
    }

    private void setBackground(boolean z) {
        if (!BuildCompatUtils.isAtLeastS()) {
            setBackgroundColor(z ? this.mBackgroundActivatedColor : this.mBackgroundColor);
        } else {
            this.mFrameView.setBackground(z ? this.mBackgroundOn : this.mBackgroundOff);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: com.android.settingslib.widget.MainSwitchBar.SavedState.1
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
        boolean mChecked;
        boolean mVisible;

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        private SavedState(Parcel parcel) {
            super(parcel);
            this.mChecked = ((Boolean) parcel.readValue(null)).booleanValue();
            this.mVisible = ((Boolean) parcel.readValue(null)).booleanValue();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeValue(Boolean.valueOf(this.mChecked));
            parcel.writeValue(Boolean.valueOf(this.mVisible));
        }

        public String toString() {
            return "MainSwitchBar.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.mChecked + " visible=" + this.mVisible + "}";
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mChecked = this.mSwitch.isChecked();
        savedState.mVisible = isShowing();
        return savedState;
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.mSwitch.setChecked(savedState.mChecked);
        setChecked(savedState.mChecked);
        setBackground(savedState.mChecked);
        setVisibility(savedState.mVisible ? 0 : 8);
        this.mSwitch.setOnCheckedChangeListener(savedState.mVisible ? this : null);
        requestLayout();
    }
}
