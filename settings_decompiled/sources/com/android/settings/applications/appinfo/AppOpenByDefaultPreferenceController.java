package com.android.settings.applications.appinfo;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.verify.domain.DomainVerificationManager;
import android.content.pm.verify.domain.DomainVerificationUserState;
import android.os.UserHandle;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.intentpicker.AppLaunchSettings;
import com.android.settings.applications.intentpicker.IntentPickerUtils;
import com.android.settingslib.R$string;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
/* loaded from: classes.dex */
public class AppOpenByDefaultPreferenceController extends AppInfoPreferenceControllerBase {
    private final DomainVerificationManager mDomainVerificationManager;
    private String mPackageName;

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppOpenByDefaultPreferenceController(Context context, String str) {
        super(context, str);
        this.mDomainVerificationManager = (DomainVerificationManager) context.getSystemService(DomainVerificationManager.class);
    }

    public AppOpenByDefaultPreferenceController setPackageName(String str) {
        this.mPackageName = str;
        return this;
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        ApplicationInfo applicationInfo;
        super.displayPreference(preferenceScreen);
        ApplicationsState.AppEntry appEntry = this.mParent.getAppEntry();
        if (appEntry == null || (applicationInfo = appEntry.info) == null) {
            this.mPreference.setEnabled(false);
        } else if ((applicationInfo.flags & 8388608) == 0 || !applicationInfo.enabled) {
            this.mPreference.setEnabled(false);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        PackageInfo packageInfo = this.mParent.getPackageInfo();
        if (packageInfo != null && !AppUtils.isInstant(packageInfo.applicationInfo) && !AppUtils.isBrowserApp(this.mContext, packageInfo.packageName, UserHandle.myUserId())) {
            preference.setVisible(true);
            preference.setSummary(getSubtext());
            return;
        }
        preference.setVisible(false);
    }

    @Override // com.android.settings.applications.appinfo.AppInfoPreferenceControllerBase
    protected Class<? extends SettingsPreferenceFragment> getDetailFragmentClass() {
        return AppLaunchSettings.class;
    }

    CharSequence getSubtext() {
        return this.mContext.getText(isLinkHandlingAllowed() ? R$string.app_link_open_always : R$string.app_link_open_never);
    }

    boolean isLinkHandlingAllowed() {
        DomainVerificationUserState domainVerificationUserState = IntentPickerUtils.getDomainVerificationUserState(this.mDomainVerificationManager, this.mPackageName);
        if (domainVerificationUserState == null) {
            return false;
        }
        return domainVerificationUserState.isLinkHandlingAllowed();
    }
}
