package com.android.settings.fuelgauge;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.Utils;
import com.android.settings.fuelgauge.batterytip.BatteryTipLoader;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.widget.LayoutPreference;
import java.util.List;
/* loaded from: classes.dex */
public class PowerUsageSummary extends PowerUsageBase implements BatteryTipPreferenceController.BatteryTipListener {
    static final int BATTERY_INFO_LOADER = 1;
    static final int BATTERY_TIP_LOADER = 2;
    static final String KEY_BATTERY_ERROR = "battery_help_message";
    static final String KEY_BATTERY_USAGE = "battery_usage_summary";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.power_usage_summary) { // from class: com.android.settings.fuelgauge.PowerUsageSummary.4
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return context.getResources().getBoolean(R$bool.config_show_top_level_battery);
        }
    };
    private final boolean isSupportUnisocPowerController;
    BatteryHeaderPreferenceController mBatteryHeaderPreferenceController;
    BatteryInfo mBatteryInfo;
    LoaderManager.LoaderCallbacks<BatteryInfo> mBatteryInfoLoaderCallbacks;
    LayoutPreference mBatteryLayoutPref;
    BatteryTipPreferenceController mBatteryTipPreferenceController;
    private LoaderManager.LoaderCallbacks<List<BatteryTip>> mBatteryTipsCallbacks;
    Preference mBatteryUsagePreference;
    BatteryUtils mBatteryUtils;
    Preference mHelpPreference;
    boolean mNeedUpdateBatteryTip;
    PowerUsageFeatureProvider mPowerFeatureProvider;
    final ContentObserver mSettingsObserver;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "PowerUsageSummary";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1263;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageBase
    protected boolean isBatteryHistoryNeeded() {
        return false;
    }

    public PowerUsageSummary() {
        this.isSupportUnisocPowerController = 1 == SystemProperties.getInt("persist.sys.pwctl.enable", 1);
        this.mSettingsObserver = new ContentObserver(new Handler()) { // from class: com.android.settings.fuelgauge.PowerUsageSummary.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                PowerUsageSummary.this.restartBatteryInfoLoader();
            }
        };
        this.mBatteryInfoLoaderCallbacks = new LoaderManager.LoaderCallbacks<BatteryInfo>() { // from class: com.android.settings.fuelgauge.PowerUsageSummary.2
            @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
            public void onLoaderReset(Loader<BatteryInfo> loader) {
            }

            @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
            public Loader<BatteryInfo> onCreateLoader(int i, Bundle bundle) {
                return new BatteryInfoLoader(PowerUsageSummary.this.getContext());
            }

            @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
            public void onLoadFinished(Loader<BatteryInfo> loader, BatteryInfo batteryInfo) {
                PowerUsageSummary.this.mBatteryHeaderPreferenceController.updateHeaderPreference(batteryInfo);
                PowerUsageSummary powerUsageSummary = PowerUsageSummary.this;
                powerUsageSummary.mBatteryHeaderPreferenceController.updateHeaderByBatteryTips(powerUsageSummary.mBatteryTipPreferenceController.getCurrentBatteryTip(), batteryInfo);
                PowerUsageSummary.this.mBatteryInfo = batteryInfo;
            }
        };
        this.mBatteryTipsCallbacks = new LoaderManager.LoaderCallbacks<List<BatteryTip>>() { // from class: com.android.settings.fuelgauge.PowerUsageSummary.3
            @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
            public void onLoaderReset(Loader<List<BatteryTip>> loader) {
            }

            @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
            public Loader<List<BatteryTip>> onCreateLoader(int i, Bundle bundle) {
                return new BatteryTipLoader(PowerUsageSummary.this.getContext(), PowerUsageSummary.this.mBatteryUsageStats);
            }

            @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
            public void onLoadFinished(Loader<List<BatteryTip>> loader, List<BatteryTip> list) {
                PowerUsageSummary.this.mBatteryTipPreferenceController.updateBatteryTips(list);
                PowerUsageSummary powerUsageSummary = PowerUsageSummary.this;
                powerUsageSummary.mBatteryHeaderPreferenceController.updateHeaderByBatteryTips(powerUsageSummary.mBatteryTipPreferenceController.getCurrentBatteryTip(), PowerUsageSummary.this.mBatteryInfo);
            }
        };
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        SettingsActivity settingsActivity = (SettingsActivity) getActivity();
        BatteryHeaderPreferenceController batteryHeaderPreferenceController = (BatteryHeaderPreferenceController) use(BatteryHeaderPreferenceController.class);
        this.mBatteryHeaderPreferenceController = batteryHeaderPreferenceController;
        batteryHeaderPreferenceController.setActivity(settingsActivity);
        this.mBatteryHeaderPreferenceController.setFragment(this);
        this.mBatteryHeaderPreferenceController.setLifecycle(getSettingsLifecycle());
        BatteryTipPreferenceController batteryTipPreferenceController = (BatteryTipPreferenceController) use(BatteryTipPreferenceController.class);
        this.mBatteryTipPreferenceController = batteryTipPreferenceController;
        batteryTipPreferenceController.setActivity(settingsActivity);
        this.mBatteryTipPreferenceController.setFragment(this);
        this.mBatteryTipPreferenceController.setBatteryTipListener(new BatteryTipPreferenceController.BatteryTipListener() { // from class: com.android.settings.fuelgauge.PowerUsageSummary$$ExternalSyntheticLambda0
            @Override // com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController.BatteryTipListener
            public final void onBatteryTipHandled(BatteryTip batteryTip) {
                PowerUsageSummary.this.onBatteryTipHandled(batteryTip);
            }
        });
    }

    @Override // com.android.settings.fuelgauge.PowerUsageBase, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setAnimationAllowed(true);
        initFeatureProvider();
        initPreference();
        this.mBatteryUtils = BatteryUtils.getInstance(getContext());
        if (Utils.isBatteryPresent(getContext())) {
            restartBatteryInfoLoader();
        } else {
            this.mHelpPreference.setVisible(true);
        }
        this.mBatteryTipPreferenceController.restoreInstanceState(bundle);
        updateBatteryTipFlag(bundle);
        if (this.isSupportUnisocPowerController && UserHandle.myUserId() == 0) {
            return;
        }
        removePreference("app_battery_saver_setting");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(Settings.Global.getUriFor("battery_estimates_last_update_time"), false, this.mSettingsObserver);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        super.onPause();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.power_usage_summary;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_battery;
    }

    @Override // com.android.settings.fuelgauge.PowerUsageBase
    protected void refreshUi(int i) {
        if (getContext() != null && this.mIsBatteryPresent) {
            if (this.mNeedUpdateBatteryTip && i != 1) {
                restartBatteryTipLoader();
            } else {
                this.mNeedUpdateBatteryTip = true;
            }
            restartBatteryInfoLoader();
        }
    }

    void restartBatteryTipLoader() {
        getLoaderManager().restartLoader(2, Bundle.EMPTY, this.mBatteryTipsCallbacks);
    }

    void setBatteryLayoutPreference(LayoutPreference layoutPreference) {
        this.mBatteryLayoutPref = layoutPreference;
    }

    void initFeatureProvider() {
        Context context = getContext();
        this.mPowerFeatureProvider = FeatureFactory.getFactory(context).getPowerUsageFeatureProvider(context);
    }

    void initPreference() {
        String string;
        Preference findPreference = findPreference(KEY_BATTERY_USAGE);
        this.mBatteryUsagePreference = findPreference;
        if (this.mPowerFeatureProvider.isChartGraphEnabled(getContext())) {
            string = getString(R$string.advanced_battery_preference_summary_with_hours);
        } else {
            string = getString(R$string.advanced_battery_preference_summary);
        }
        findPreference.setSummary(string);
        Preference findPreference2 = findPreference(KEY_BATTERY_ERROR);
        this.mHelpPreference = findPreference2;
        findPreference2.setVisible(false);
    }

    void restartBatteryInfoLoader() {
        if (getContext() != null && this.mIsBatteryPresent) {
            getLoaderManager().restartLoader(1, Bundle.EMPTY, this.mBatteryInfoLoaderCallbacks);
        }
    }

    void updateBatteryTipFlag(Bundle bundle) {
        this.mNeedUpdateBatteryTip = bundle == null || this.mBatteryTipPreferenceController.needUpdate();
    }

    @Override // com.android.settings.fuelgauge.PowerUsageBase
    protected void restartBatteryStatsLoader(int i) {
        super.restartBatteryStatsLoader(i);
        if (this.mIsBatteryPresent) {
            this.mBatteryHeaderPreferenceController.quickUpdateHeaderPreference();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mBatteryTipPreferenceController.saveInstanceState(bundle);
    }

    @Override // com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController.BatteryTipListener
    public void onBatteryTipHandled(BatteryTip batteryTip) {
        restartBatteryTipLoader();
    }
}
