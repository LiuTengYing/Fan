package com.android.settings.accessibility;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.preference.PreferenceViewHolder;
import com.android.settingslib.widget.FooterPreference;
/* loaded from: classes.dex */
public final class AccessibilityFooterPreference extends FooterPreference {
    private boolean mLinkEnabled;

    public AccessibilityFooterPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public AccessibilityFooterPreference(Context context) {
        super(context);
    }

    @Override // com.android.settingslib.widget.FooterPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        TextView textView = (TextView) preferenceViewHolder.itemView.findViewById(16908310);
        if (this.mLinkEnabled) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setFocusable(false);
            return;
        }
        textView.setMovementMethod(null);
    }

    public void setLinkEnabled(boolean z) {
        if (this.mLinkEnabled != z) {
            this.mLinkEnabled = z;
            notifyChanged();
        }
    }
}
