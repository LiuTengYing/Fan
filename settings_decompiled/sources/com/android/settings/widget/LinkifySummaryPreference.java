package com.android.settings.widget;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
/* loaded from: classes.dex */
public class LinkifySummaryPreference extends Preference {
    public LinkifySummaryPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public LinkifySummaryPreference(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.findViewById(16908304);
        if (textView == null || textView.getVisibility() != 0) {
            return;
        }
        CharSequence summary = getSummary();
        if (TextUtils.isEmpty(summary)) {
            return;
        }
        SpannableString spannableString = new SpannableString(summary);
        if (((ClickableSpan[]) spannableString.getSpans(0, spannableString.length(), ClickableSpan.class)).length > 0) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
