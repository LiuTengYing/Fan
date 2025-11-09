package com.android.settings.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.PreferenceViewHolder;
import com.android.settings.widget.HomepagePreferenceLayoutHelper;
import com.android.settingslib.RestrictedTopLevelPreference;
/* loaded from: classes.dex */
public class RestrictedHomepagePreference extends RestrictedTopLevelPreference implements HomepagePreferenceLayoutHelper.HomepagePreferenceLayout {
    private final HomepagePreferenceLayoutHelper mHelper;

    public RestrictedHomepagePreference(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mHelper = new HomepagePreferenceLayoutHelper(this);
    }

    public RestrictedHomepagePreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mHelper = new HomepagePreferenceLayoutHelper(this);
    }

    public RestrictedHomepagePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mHelper = new HomepagePreferenceLayoutHelper(this);
    }

    @Override // com.android.settingslib.RestrictedTopLevelPreference, androidx.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder preferenceViewHolder) {
        super.onBindViewHolder(preferenceViewHolder);
        this.mHelper.onBindViewHolder(preferenceViewHolder);
    }

    @Override // com.android.settings.widget.HomepagePreferenceLayoutHelper.HomepagePreferenceLayout
    public HomepagePreferenceLayoutHelper getHelper() {
        return this.mHelper;
    }
}
