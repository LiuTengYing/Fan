package com.android.settings.enterprise;

import android.content.Context;
import android.os.Bundle;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.enterprise.ApplicationListPreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public abstract class ApplicationListFragment extends DashboardFragment implements ApplicationListPreferenceController.ApplicationListBuilder {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "EnterprisePrivacySettings";
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        replaceEnterpriseStringTitle("enterprise_privacy_apps_footer", "Settings.ADMIN_ACTION_APPS_COUNT_ESTIMATED", R$string.enterprise_privacy_apps_count_estimation_info);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.app_list_disclosure_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ApplicationListPreferenceController(context, this, context.getPackageManager(), this));
        return arrayList;
    }

    /* loaded from: classes.dex */
    private static abstract class AdminGrantedPermission extends ApplicationListFragment {
        private final String[] mPermissions;

        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 939;
        }

        public AdminGrantedPermission(String[] strArr) {
            this.mPermissions = strArr;
        }

        @Override // com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            FeatureFactory.getFactory(context).getApplicationFeatureProvider(context).listAppsWithAdminGrantedPermissions(this.mPermissions, listOfAppsCallback);
        }
    }

    /* loaded from: classes.dex */
    public static class AdminGrantedPermissionCamera extends AdminGrantedPermission {
        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public /* bridge */ /* synthetic */ void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super.buildApplicationList(context, listOfAppsCallback);
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settingslib.core.instrumentation.Instrumentable
        public /* bridge */ /* synthetic */ int getMetricsCategory() {
            return super.getMetricsCategory();
        }

        public AdminGrantedPermissionCamera() {
            super(new String[]{"android.permission.CAMERA"});
        }
    }

    /* loaded from: classes.dex */
    public static class AdminGrantedPermissionLocation extends AdminGrantedPermission {
        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public /* bridge */ /* synthetic */ void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super.buildApplicationList(context, listOfAppsCallback);
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settingslib.core.instrumentation.Instrumentable
        public /* bridge */ /* synthetic */ int getMetricsCategory() {
            return super.getMetricsCategory();
        }

        public AdminGrantedPermissionLocation() {
            super(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"});
        }
    }

    /* loaded from: classes.dex */
    public static class AdminGrantedPermissionMicrophone extends AdminGrantedPermission {
        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public /* bridge */ /* synthetic */ void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            super.buildApplicationList(context, listOfAppsCallback);
        }

        @Override // com.android.settings.enterprise.ApplicationListFragment.AdminGrantedPermission, com.android.settingslib.core.instrumentation.Instrumentable
        public /* bridge */ /* synthetic */ int getMetricsCategory() {
            return super.getMetricsCategory();
        }

        public AdminGrantedPermissionMicrophone() {
            super(new String[]{"android.permission.RECORD_AUDIO"});
        }
    }

    /* loaded from: classes.dex */
    public static class EnterpriseInstalledPackages extends ApplicationListFragment {
        @Override // com.android.settingslib.core.instrumentation.Instrumentable
        public int getMetricsCategory() {
            return 938;
        }

        @Override // com.android.settings.enterprise.ApplicationListPreferenceController.ApplicationListBuilder
        public void buildApplicationList(Context context, ApplicationFeatureProvider.ListOfAppsCallback listOfAppsCallback) {
            FeatureFactory.getFactory(context).getApplicationFeatureProvider(context).listPolicyInstalledApps(listOfAppsCallback);
        }
    }
}
