package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
/* loaded from: classes2.dex */
public class ButtonPreference extends Preference {
    private Button mButton;
    private View.OnClickListener mClickListener;
    private int mGravity;
    private Drawable mIcon;
    private CharSequence mTitle;

    public ButtonPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(context, attributeSet, i);
    }

    public ButtonPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    private void init(Context context, AttributeSet attributeSet, int i) {
        setLayoutResource(R$layout.settingslib_button_layout);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, androidx.preference.R$styleable.Preference, i, 0);
            this.mTitle = obtainStyledAttributes.getText(androidx.preference.R$styleable.Preference_android_title);
            this.mIcon = obtainStyledAttributes.getDrawable(androidx.preference.R$styleable.Preference_android_icon);
            obtainStyledAttributes.recycle();
            TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, R$styleable.ButtonPreference, i, 0);
            this.mGravity = obtainStyledAttributes2.getInt(R$styleable.ButtonPreference_android_gravity, 8388611);
            obtainStyledAttributes2.recycle();
        }
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        this.mButton = (Button) preferenceViewHolder.findViewById(R$id.settingslib_button);
        setTitle(this.mTitle);
        setIcon(this.mIcon);
        setGravity(this.mGravity);
        setOnClickListener(this.mClickListener);
        if (this.mButton != null) {
            boolean isSelectable = isSelectable();
            this.mButton.setFocusable(isSelectable);
            this.mButton.setClickable(isSelectable);
            this.mButton.setEnabled(isEnabled());
        }
        preferenceViewHolder.setDividerAllowedAbove(false);
        preferenceViewHolder.setDividerAllowedBelow(false);
    }

    @Override // androidx.preference.Preference
    public void setTitle(CharSequence charSequence) {
        this.mTitle = charSequence;
        Button button = this.mButton;
        if (button != null) {
            button.setText(charSequence);
        }
    }

    @Override // androidx.preference.Preference
    public void setIcon(Drawable drawable) {
        this.mIcon = drawable;
        if (this.mButton == null || drawable == null) {
            return;
        }
        int applyDimension = (int) TypedValue.applyDimension(1, 24.0f, getContext().getResources().getDisplayMetrics());
        drawable.setBounds(0, 0, applyDimension, applyDimension);
        this.mButton.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
    }

    @Override // androidx.preference.Preference
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        Button button = this.mButton;
        if (button != null) {
            button.setEnabled(z);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mClickListener = onClickListener;
        Button button = this.mButton;
        if (button != null) {
            button.setOnClickListener(onClickListener);
        }
    }

    public void setGravity(int i) {
        if (i == 1 || i == 16 || i == 17) {
            this.mGravity = 1;
        } else {
            this.mGravity = 8388611;
        }
        Button button = this.mButton;
        if (button != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
            layoutParams.gravity = this.mGravity;
            this.mButton.setLayoutParams(layoutParams);
        }
    }
}
