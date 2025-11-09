package com.android.settingslib.core;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.os.BuildCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import java.util.function.Supplier;
/* loaded from: classes2.dex */
public abstract class AbstractPreferenceController {
    private static final String TAG = "AbstractPrefController";
    protected final Context mContext;
    private final DevicePolicyManager mDevicePolicyManager;

    public abstract String getPreferenceKey();

    public CharSequence getSummary() {
        return null;
    }

    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }

    public abstract boolean isAvailable();

    public AbstractPreferenceController(Context context) {
        this.mContext = context;
        this.mDevicePolicyManager = (DevicePolicyManager) context.getSystemService("device_policy");
    }

    public void displayPreference(PreferenceScreen preferenceScreen) {
        String preferenceKey = getPreferenceKey();
        if (TextUtils.isEmpty(preferenceKey)) {
            Log.w(TAG, "Skipping displayPreference because key is empty:" + getClass().getName());
        } else if (isAvailable()) {
            setVisible(preferenceScreen, preferenceKey, true);
            if (this instanceof Preference.OnPreferenceChangeListener) {
                preferenceScreen.findPreference(preferenceKey).setOnPreferenceChangeListener((Preference.OnPreferenceChangeListener) this);
            }
        } else {
            setVisible(preferenceScreen, preferenceKey, false);
        }
    }

    public void updateState(Preference preference) {
        refreshSummary(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void refreshSummary(Preference preference) {
        CharSequence summary;
        if (preference == null || (summary = getSummary()) == null) {
            return;
        }
        preference.setSummary(summary);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void setVisible(PreferenceGroup preferenceGroup, String str, boolean z) {
        Preference findPreference = preferenceGroup.findPreference(str);
        if (findPreference != null) {
            findPreference.setVisible(z);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void replaceEnterpriseStringTitle(PreferenceScreen preferenceScreen, String str, String str2, final int i) {
        if (!BuildCompat.isAtLeastT() || this.mDevicePolicyManager == null) {
            return;
        }
        Preference findPreference = preferenceScreen.findPreference(str);
        if (findPreference == null) {
            Log.d(TAG, "Could not find enterprise preference " + str);
            return;
        }
        findPreference.setTitle(this.mDevicePolicyManager.getResources().getString(str2, new Supplier() { // from class: com.android.settingslib.core.AbstractPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$replaceEnterpriseStringTitle$0;
                lambda$replaceEnterpriseStringTitle$0 = AbstractPreferenceController.this.lambda$replaceEnterpriseStringTitle$0(i);
                return lambda$replaceEnterpriseStringTitle$0;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$replaceEnterpriseStringTitle$0(int i) {
        return this.mContext.getString(i);
    }

    protected void replaceEnterpriseStringSummary(PreferenceScreen preferenceScreen, String str, String str2, final int i) {
        if (!BuildCompat.isAtLeastT() || this.mDevicePolicyManager == null) {
            return;
        }
        Preference findPreference = preferenceScreen.findPreference(str);
        if (findPreference == null) {
            Log.d(TAG, "Could not find enterprise preference " + str);
            return;
        }
        findPreference.setSummary(this.mDevicePolicyManager.getResources().getString(str2, new Supplier() { // from class: com.android.settingslib.core.AbstractPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Supplier
            public final Object get() {
                String lambda$replaceEnterpriseStringSummary$1;
                lambda$replaceEnterpriseStringSummary$1 = AbstractPreferenceController.this.lambda$replaceEnterpriseStringSummary$1(i);
                return lambda$replaceEnterpriseStringSummary$1;
            }
        }));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$replaceEnterpriseStringSummary$1(int i) {
        return this.mContext.getString(i);
    }
}
