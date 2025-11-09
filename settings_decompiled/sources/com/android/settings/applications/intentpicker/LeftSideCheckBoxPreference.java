package com.android.settings.applications.intentpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$layout;
import com.android.settingslib.widget.TwoTargetPreference;
/* loaded from: classes.dex */
public class LeftSideCheckBoxPreference extends TwoTargetPreference {
    private CheckBox mCheckBox;
    private boolean mChecked;

    public LeftSideCheckBoxPreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        setLayoutResource(R$layout.preference_checkable_two_target);
    }

    public LeftSideCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public LeftSideCheckBoxPreference(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LeftSideCheckBoxPreference(Context context, boolean z) {
        super(context);
        this.mChecked = z;
        setLayoutResource(R$layout.preference_checkable_two_target);
    }

    @Override // com.android.settingslib.widget.TwoTargetPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        CheckBox checkBox = (CheckBox) preferenceViewHolder.findViewById(16908289);
        this.mCheckBox = checkBox;
        if (checkBox != null) {
            checkBox.setChecked(this.mChecked);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.preference.Preference
    public void onClick() {
        CheckBox checkBox = this.mCheckBox;
        if (checkBox != null) {
            boolean z = !this.mChecked;
            this.mChecked = z;
            checkBox.setChecked(z);
            callChangeListener(Boolean.valueOf(this.mChecked));
        }
    }
}
