package com.android.settings.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.R$styleable;
/* loaded from: classes.dex */
public class FixedLineSummaryPreference extends Preference {
    private int mSummaryLineCount;

    public FixedLineSummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R$styleable.FixedLineSummaryPreference, 0, 0);
        int i = R$styleable.FixedLineSummaryPreference_summaryLineCount;
        if (obtainStyledAttributes.hasValue(i)) {
            this.mSummaryLineCount = obtainStyledAttributes.getInteger(i, 1);
        } else {
            this.mSummaryLineCount = 1;
        }
        obtainStyledAttributes.recycle();
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView != null) {
            textView.setMinLines(this.mSummaryLineCount);
            textView.setMaxLines(this.mSummaryLineCount);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        }
    }
}
