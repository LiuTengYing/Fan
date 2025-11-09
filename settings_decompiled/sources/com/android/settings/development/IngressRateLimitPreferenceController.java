package com.android.settings.development;

import android.content.Context;
import android.net.ConnectivitySettingsManager;
import android.util.Log;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
/* loaded from: classes.dex */
public class IngressRateLimitPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin {
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "ingress_rate_limit";
    }

    public IngressRateLimitPreferenceController(Context context) {
        super(context);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            ConnectivitySettingsManager.setIngressRateLimitInBytesPerSecond(this.mContext, Long.parseLong(obj.toString()));
            return true;
        } catch (IllegalArgumentException e) {
            Log.e("IngressRateLimitPreferenceController", "invalid rate limit", e);
            return false;
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String valueOf = String.valueOf(ConnectivitySettingsManager.getIngressRateLimitInBytesPerSecond(this.mContext));
        ListPreference listPreference = (ListPreference) preference;
        for (CharSequence charSequence : listPreference.getEntryValues()) {
            if (valueOf.contentEquals(charSequence)) {
                listPreference.setValue(valueOf);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
        ConnectivitySettingsManager.setIngressRateLimitInBytesPerSecond(this.mContext, -1L);
        ((ListPreference) this.mPreference).setValue(String.valueOf(-1));
    }
}
