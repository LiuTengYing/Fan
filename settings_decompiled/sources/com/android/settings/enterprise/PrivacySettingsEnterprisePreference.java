package com.android.settings.enterprise;

import android.content.Context;
import android.provider.SearchIndexableResource;
import com.android.settings.R$xml;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/* loaded from: classes.dex */
public class PrivacySettingsEnterprisePreference implements PrivacySettingsPreference {
    private final Context mContext;

    public PrivacySettingsEnterprisePreference(Context context) {
        this.mContext = context.getApplicationContext();
    }

    @Override // com.android.settings.enterprise.PrivacySettingsPreference
    public int getPreferenceScreenResId() {
        return R$xml.enterprise_privacy_settings;
    }

    @Override // com.android.settings.enterprise.PrivacySettingsPreference
    public List<SearchIndexableResource> getXmlResourcesToIndex() {
        SearchIndexableResource searchIndexableResource = new SearchIndexableResource(this.mContext);
        searchIndexableResource.xmlResId = getPreferenceScreenResId();
        return Collections.singletonList(searchIndexableResource);
    }

    @Override // com.android.settings.enterprise.PrivacySettingsPreference
    public List<AbstractPreferenceController> createPreferenceControllers(boolean z) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new NetworkLogsPreferenceController(this.mContext));
        arrayList.add(new BugReportsPreferenceController(this.mContext));
        arrayList.add(new SecurityLogsPreferenceController(this.mContext));
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add(new EnterpriseInstalledPackagesPreferenceController(this.mContext, z));
        arrayList2.add(new AdminGrantedLocationPermissionsPreferenceController(this.mContext, z));
        arrayList2.add(new AdminGrantedMicrophonePermissionPreferenceController(this.mContext, z));
        arrayList2.add(new AdminGrantedCameraPermissionPreferenceController(this.mContext, z));
        arrayList2.add(new EnterpriseSetDefaultAppsPreferenceController(this.mContext));
        arrayList2.add(new AlwaysOnVpnCurrentUserPreferenceController(this.mContext));
        arrayList2.add(new AlwaysOnVpnManagedProfilePreferenceController(this.mContext));
        arrayList2.add(new ImePreferenceController(this.mContext));
        arrayList2.add(new GlobalHttpProxyPreferenceController(this.mContext));
        arrayList2.add(new CaCertsCurrentUserPreferenceController(this.mContext));
        arrayList2.add(new CaCertsManagedProfilePreferenceController(this.mContext));
        arrayList.addAll(arrayList2);
        arrayList.add(new PreferenceCategoryController(this.mContext, "exposure_changes_category").setChildren(arrayList2));
        arrayList.add(new FailedPasswordWipeCurrentUserPreferenceController(this.mContext));
        arrayList.add(new FailedPasswordWipeManagedProfilePreferenceController(this.mContext));
        return arrayList;
    }
}
