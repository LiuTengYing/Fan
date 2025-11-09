package com.android.settings.widget;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
/* loaded from: classes.dex */
public class MyPreferenceCategory extends PreferenceCategory {
    public MyPreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.preference.Preference
    protected void onBindView(View view) {
        super.onBindView(view);
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setTextSize(12.0f);
            textView.setAllCaps(false);
            textView.setTextColor(Color.parseColor("#000000"));
        }
    }
}
