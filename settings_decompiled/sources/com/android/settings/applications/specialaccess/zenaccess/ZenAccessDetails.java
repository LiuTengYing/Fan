package com.android.settings.applications.specialaccess.zenaccess;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.AppInfoWithHeader;
import com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin;
/* loaded from: classes.dex */
public class ZenAccessDetails extends AppInfoWithHeader implements ZenAccessSettingObserverMixin.Listener {
    @Override // com.android.settings.applications.AppInfoBase
    protected AlertDialog createDialog(int i, int i2) {
        return null;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1692;
    }

    @Override // com.android.settings.applications.AppInfoBase, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        addPreferencesFromResource(R$xml.zen_access_permission_details);
        getSettingsLifecycle().addObserver(new ZenAccessSettingObserverMixin(getContext(), this));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.applications.AppInfoBase
    public boolean refreshUi() {
        Context context = getContext();
        if (ZenAccessController.getPackagesRequestingNotificationPolicyAccess().contains(this.mPackageName)) {
            updatePreference(context, (SwitchPreference) findPreference("zen_access_switch"));
            return true;
        }
        finish();
        return true;
    }

    public void updatePreference(Context context, SwitchPreference switchPreference) {
        final CharSequence loadLabel = this.mPackageInfo.applicationInfo.loadLabel(this.mPm);
        if (ZenAccessController.getAutoApprovedPackages(context).contains(this.mPackageName)) {
            switchPreference.setEnabled(false);
            switchPreference.setSummary(getString(R$string.zen_access_disabled_package_warning));
            return;
        }
        switchPreference.setChecked(ZenAccessController.hasAccess(context, this.mPackageName));
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.applications.specialaccess.zenaccess.ZenAccessDetails$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceChangeListener
            public final boolean onPreferenceChange(Preference preference, Object obj) {
                boolean lambda$updatePreference$0;
                lambda$updatePreference$0 = ZenAccessDetails.this.lambda$updatePreference$0(loadLabel, preference, obj);
                return lambda$updatePreference$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updatePreference$0(CharSequence charSequence, Preference preference, Object obj) {
        if (((Boolean) obj).booleanValue()) {
            new ScaryWarningDialogFragment().setPkgInfo(this.mPackageName, charSequence, this).show(getFragmentManager(), "dialog");
            return false;
        }
        new FriendlyWarningDialogFragment().setPkgInfo(this.mPackageName, charSequence, this).show(getFragmentManager(), "dialog");
        return false;
    }

    @Override // com.android.settings.applications.specialaccess.zenaccess.ZenAccessSettingObserverMixin.Listener
    public void onZenAccessPolicyChanged() {
        refreshUi();
    }
}
