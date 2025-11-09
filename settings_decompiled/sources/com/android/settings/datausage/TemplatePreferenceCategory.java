package com.android.settings.datausage;

import android.content.Context;
import android.net.NetworkTemplate;
import android.util.AttributeSet;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.settings.datausage.TemplatePreference;
/* loaded from: classes.dex */
public class TemplatePreferenceCategory extends PreferenceCategory implements TemplatePreference {
    private int mSubId;
    private NetworkTemplate mTemplate;

    public TemplatePreferenceCategory(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // com.android.settings.datausage.TemplatePreference
    public void setTemplate(NetworkTemplate networkTemplate, int i, TemplatePreference.NetworkServices networkServices) {
        this.mTemplate = networkTemplate;
        this.mSubId = i;
    }

    @Override // androidx.preference.PreferenceGroup
    public boolean addPreference(Preference preference) {
        if (!(preference instanceof TemplatePreference)) {
            throw new IllegalArgumentException("TemplatePreferenceCategories can only hold TemplatePreferences");
        }
        return super.addPreference(preference);
    }

    public void pushTemplates(TemplatePreference.NetworkServices networkServices) {
        if (this.mTemplate == null) {
            throw new RuntimeException("null mTemplate for " + getKey());
        }
        for (int i = 0; i < getPreferenceCount(); i++) {
            ((TemplatePreference) getPreference(i)).setTemplate(this.mTemplate, this.mSubId, networkServices);
        }
    }
}
