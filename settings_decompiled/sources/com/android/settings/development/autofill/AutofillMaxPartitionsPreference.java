package com.android.settings.development.autofill;

import android.content.Context;
import android.util.AttributeSet;
/* loaded from: classes.dex */
public final class AutofillMaxPartitionsPreference extends AbstractGlobalSettingsPreference {
    @Override // com.android.settings.development.autofill.AbstractGlobalSettingsPreference, androidx.preference.Preference
    public /* bridge */ /* synthetic */ void onAttached() {
        super.onAttached();
    }

    @Override // com.android.settings.development.autofill.AbstractGlobalSettingsPreference, androidx.preference.Preference
    public /* bridge */ /* synthetic */ void onDetached() {
        super.onDetached();
    }

    public AutofillMaxPartitionsPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, "autofill_max_partitions_size", 10);
    }
}
