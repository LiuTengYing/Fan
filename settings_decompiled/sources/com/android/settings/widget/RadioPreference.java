package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.CheckBoxPreference;
import com.android.settings.R$layout;
/* loaded from: classes.dex */
public class RadioPreference extends CheckBoxPreference {
    public RadioPreference(Context context) {
        this(context, null);
    }

    public RadioPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setWidgetLayoutResource(R$layout.radio_preference_widget);
    }
}
