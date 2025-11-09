package com.android.settings.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RadioButton;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$id;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class NavBarRadioPreference extends Preference {
    private boolean mChecked;
    private Drawable mIcon;
    private RadioButton mRadioButton;

    public NavBarRadioPreference(Context context) {
        this(context, null);
    }

    public NavBarRadioPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setLayoutResource(R$layout.customized_nav_bar_item);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        Drawable drawable;
        super.onBindViewHolder(preferenceViewHolder);
        ImageView imageView = (ImageView) preferenceViewHolder.findViewById(R$id.keyImage);
        if (imageView != null && (drawable = this.mIcon) != null) {
            imageView.setImageDrawable(drawable);
        }
        RadioButton radioButton = (RadioButton) preferenceViewHolder.findViewById(R$id.button);
        this.mRadioButton = radioButton;
        if (radioButton != null) {
            radioButton.setChecked(this.mChecked);
        }
    }

    @Override // androidx.preference.Preference
    public void setIcon(int i) {
        Drawable drawable = getContext().getDrawable(i);
        if ((drawable != null || this.mIcon == null) && (drawable == null || drawable.equals(this.mIcon))) {
            return;
        }
        this.mIcon = drawable;
        notifyChanged();
    }

    public void setChecked(boolean z) {
        this.mChecked = z;
        notifyChanged();
    }
}
