package com.android.settings.wifi.calling;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.core.text.util.LinkifyCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
/* loaded from: classes.dex */
public class LinkifyDescriptionPreference extends Preference {
    public LinkifyDescriptionPreference(Context context) {
        this(context, null);
    }

    public LinkifyDescriptionPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
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
        textView.setMaxLines(Integer.MAX_VALUE);
        SpannableString spannableString = new SpannableString(summary);
        if (((ClickableSpan[]) spannableString.getSpans(0, spannableString.length(), ClickableSpan.class)).length > 0) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
        }
        LinkifyCompat.addLinks(textView, 7);
    }
}
