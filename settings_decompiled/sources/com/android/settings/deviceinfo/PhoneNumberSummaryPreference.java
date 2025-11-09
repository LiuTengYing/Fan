package com.android.settings.deviceinfo;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
/* loaded from: classes.dex */
public class PhoneNumberSummaryPreference extends Preference {
    public PhoneNumberSummaryPreference(Context context) {
        this(context, null);
    }

    public PhoneNumberSummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView != null) {
            textView.setText(PhoneNumberUtil.expandByTts(textView.getText()));
        }
    }
}
