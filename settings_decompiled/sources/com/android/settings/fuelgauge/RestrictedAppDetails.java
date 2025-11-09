package com.android.settings.fuelgauge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.util.SparseLongArray;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.fuelgauge.batterytip.AppInfo;
import com.android.settings.fuelgauge.batterytip.BatteryDatabaseManager;
import com.android.settings.fuelgauge.batterytip.BatteryTipDialogFragment;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.fuelgauge.batterytip.tips.RestrictAppTip;
import com.android.settings.fuelgauge.batterytip.tips.UnrestrictAppTip;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.AppCheckBoxPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.StringUtil;
import java.util.List;
/* loaded from: classes.dex */
public class RestrictedAppDetails extends DashboardFragment implements BatteryTipPreferenceController.BatteryTipListener {
    static final String EXTRA_APP_INFO_LIST = "app_info_list";
    List<AppInfo> mAppInfos;
    BatteryDatabaseManager mBatteryDatabaseManager;
    BatteryUtils mBatteryUtils;
    IconDrawableFactory mIconDrawableFactory;
    private MetricsFeatureProvider mMetricsFeatureProvider;
    PackageManager mPackageManager;
    PreferenceGroup mRestrictedAppListGroup;

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "RestrictedAppDetails";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1285;
    }

    public static void startRestrictedAppDetails(InstrumentedPreferenceFragment instrumentedPreferenceFragment, List<AppInfo> list) {
        Bundle bundle = new Bundle();
        bundle.putParcelableList(EXTRA_APP_INFO_LIST, list);
        new SubSettingLauncher(instrumentedPreferenceFragment.getContext()).setDestination(RestrictedAppDetails.class.getName()).setArguments(bundle).setTitleRes(R$string.restricted_app_title).setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory()).launch();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mRestrictedAppListGroup = (PreferenceGroup) findPreference("restrict_app_list");
        this.mAppInfos = getArguments().getParcelableArrayList(EXTRA_APP_INFO_LIST);
        this.mPackageManager = context.getPackageManager();
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(context);
        this.mBatteryUtils = BatteryUtils.getInstance(context);
        this.mBatteryDatabaseManager = BatteryDatabaseManager.getInstance(context);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
        refreshUi();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.restricted_apps_detail;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_restricted_apps;
    }

    void refreshUi() {
        long j;
        this.mRestrictedAppListGroup.removeAll();
        Context prefContext = getPrefContext();
        SparseLongArray queryActionTime = this.mBatteryDatabaseManager.queryActionTime(0);
        long currentTimeMillis = System.currentTimeMillis();
        int size = this.mAppInfos.size();
        for (int i = 0; i < size; i++) {
            AppCheckBoxPreference appCheckBoxPreference = new AppCheckBoxPreference(prefContext);
            final AppInfo appInfo = this.mAppInfos.get(i);
            try {
                ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(appInfo.packageName, 0, UserHandle.getUserId(appInfo.uid));
                appCheckBoxPreference.setChecked(this.mBatteryUtils.isForceAppStandbyEnabled(appInfo.uid, appInfo.packageName));
                appCheckBoxPreference.setTitle(this.mPackageManager.getApplicationLabel(applicationInfoAsUser));
                appCheckBoxPreference.setIcon(Utils.getBadgedIcon(this.mIconDrawableFactory, this.mPackageManager, appInfo.packageName, UserHandle.getUserId(appInfo.uid)));
                appCheckBoxPreference.setKey(getKeyFromAppInfo(appInfo));
                appCheckBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.fuelgauge.RestrictedAppDetails$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean lambda$refreshUi$0;
                        lambda$refreshUi$0 = RestrictedAppDetails.this.lambda$refreshUi$0(appInfo, preference, obj);
                        return lambda$refreshUi$0;
                    }
                });
                if (queryActionTime.get(appInfo.uid, -1L) != -1) {
                    appCheckBoxPreference.setSummary(getString(R$string.restricted_app_time_summary, StringUtil.formatRelativeTime(prefContext, currentTimeMillis - j, false)));
                }
                appCheckBoxPreference.getSummaryOn();
                this.mRestrictedAppListGroup.addPreference(appCheckBoxPreference);
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("RestrictedAppDetails", "Can't find package: " + appInfo.packageName);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$refreshUi$0(AppInfo appInfo, Preference preference, Object obj) {
        BatteryTipDialogFragment createDialogFragment = createDialogFragment(appInfo, ((Boolean) obj).booleanValue());
        createDialogFragment.setTargetFragment(this, 0);
        createDialogFragment.show(getFragmentManager(), "RestrictedAppDetails");
        this.mMetricsFeatureProvider.action(getContext(), 1780, appInfo.packageName);
        return false;
    }

    @Override // com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController.BatteryTipListener
    public void onBatteryTipHandled(BatteryTip batteryTip) {
        AppInfo unrestrictAppInfo;
        boolean z = batteryTip instanceof RestrictAppTip;
        if (z) {
            unrestrictAppInfo = ((RestrictAppTip) batteryTip).getRestrictAppList().get(0);
        } else {
            unrestrictAppInfo = ((UnrestrictAppTip) batteryTip).getUnrestrictAppInfo();
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) this.mRestrictedAppListGroup.findPreference(getKeyFromAppInfo(unrestrictAppInfo));
        if (checkBoxPreference != null) {
            checkBoxPreference.setChecked(z);
        }
    }

    BatteryTipDialogFragment createDialogFragment(AppInfo appInfo, boolean z) {
        BatteryTip unrestrictAppTip;
        if (z) {
            unrestrictAppTip = new RestrictAppTip(0, appInfo);
        } else {
            unrestrictAppTip = new UnrestrictAppTip(0, appInfo);
        }
        return BatteryTipDialogFragment.newInstance(unrestrictAppTip, getMetricsCategory());
    }

    String getKeyFromAppInfo(AppInfo appInfo) {
        return appInfo.uid + "," + appInfo.packageName;
    }
}
