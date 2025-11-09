package com.android.settings.fuelgauge;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.appinfo.AppButtonsPreferenceController;
import com.android.settings.applications.appinfo.ButtonActionDialogFragment;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController;
import com.android.settings.fuelgauge.batterytip.tips.BatteryTip;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.EntityHeaderController;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.utils.StringUtil;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class AdvancedPowerUsageDetail extends DashboardFragment implements ButtonActionDialogFragment.AppButtonsDialogListener, BatteryTipPreferenceController.BatteryTipListener, SelectorWithWidgetPreference.OnClickListener {
    private AppButtonsPreferenceController mAppButtonsPreferenceController;
    ApplicationsState.AppEntry mAppEntry;
    private BackgroundActivityPreferenceController mBackgroundActivityPreferenceController;
    Preference mBackgroundPreference;
    BackupManager mBackupManager;
    BatteryOptimizeUtils mBatteryOptimizeUtils;
    BatteryUtils mBatteryUtils;
    FooterPreference mFooterPreference;
    Preference mForegroundPreference;
    LayoutPreference mHeaderPreference;
    SelectorWithWidgetPreference mOptimizePreference;
    SelectorWithWidgetPreference mRestrictedPreference;
    ApplicationsState mState;
    SelectorWithWidgetPreference mUnrestrictedPreference;
    boolean mEnableTriState = true;
    int mOptimizationMode = 0;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AdvancedPowerDetail";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 53;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class LaunchBatteryDetailPageArgs {
        private String mAppLabel;
        private long mBackgroundTimeMs;
        private int mConsumedPower;
        private long mForegroundTimeMs;
        private int mIconId;
        private boolean mIsUserEntry;
        private String mPackageName;
        private String mSlotInformation;
        private int mUid;
        private String mUsagePercent;

        private LaunchBatteryDetailPageArgs() {
        }
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, BatteryDiffEntry batteryDiffEntry, String str, boolean z, String str2) {
        BatteryHistEntry batteryHistEntry = batteryDiffEntry.mBatteryHistEntry;
        LaunchBatteryDetailPageArgs launchBatteryDetailPageArgs = new LaunchBatteryDetailPageArgs();
        launchBatteryDetailPageArgs.mUsagePercent = str;
        launchBatteryDetailPageArgs.mPackageName = batteryDiffEntry.getPackageName();
        launchBatteryDetailPageArgs.mAppLabel = batteryDiffEntry.getAppLabel();
        launchBatteryDetailPageArgs.mSlotInformation = str2;
        launchBatteryDetailPageArgs.mUid = (int) batteryHistEntry.mUid;
        launchBatteryDetailPageArgs.mIconId = batteryDiffEntry.getAppIconId();
        launchBatteryDetailPageArgs.mConsumedPower = (int) batteryDiffEntry.mConsumePower;
        launchBatteryDetailPageArgs.mForegroundTimeMs = z ? batteryDiffEntry.mForegroundUsageTimeInMs : 0L;
        launchBatteryDetailPageArgs.mBackgroundTimeMs = z ? batteryDiffEntry.mBackgroundUsageTimeInMs : 0L;
        launchBatteryDetailPageArgs.mIsUserEntry = batteryHistEntry.isUserEntry();
        startBatteryDetailPage(activity, instrumentedPreferenceFragment, launchBatteryDetailPageArgs);
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, BatteryEntry batteryEntry, String str, boolean z) {
        LaunchBatteryDetailPageArgs launchBatteryDetailPageArgs = new LaunchBatteryDetailPageArgs();
        launchBatteryDetailPageArgs.mUsagePercent = str;
        launchBatteryDetailPageArgs.mPackageName = batteryEntry.getDefaultPackageName();
        launchBatteryDetailPageArgs.mAppLabel = batteryEntry.getLabel();
        launchBatteryDetailPageArgs.mUid = batteryEntry.getUid();
        launchBatteryDetailPageArgs.mIconId = batteryEntry.mIconId;
        launchBatteryDetailPageArgs.mConsumedPower = (int) batteryEntry.getConsumedPower();
        launchBatteryDetailPageArgs.mForegroundTimeMs = z ? batteryEntry.getTimeInForegroundMs() : 0L;
        launchBatteryDetailPageArgs.mBackgroundTimeMs = z ? batteryEntry.getTimeInBackgroundMs() : 0L;
        launchBatteryDetailPageArgs.mIsUserEntry = batteryEntry.isUserEntry();
        startBatteryDetailPage(activity, instrumentedPreferenceFragment, launchBatteryDetailPageArgs);
    }

    private static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, LaunchBatteryDetailPageArgs launchBatteryDetailPageArgs) {
        Bundle bundle = new Bundle();
        if (launchBatteryDetailPageArgs.mPackageName == null) {
            bundle.putString("extra_label", launchBatteryDetailPageArgs.mAppLabel);
            bundle.putInt("extra_icon_id", launchBatteryDetailPageArgs.mIconId);
            bundle.putString("extra_package_name", null);
        } else {
            bundle.putString("extra_package_name", launchBatteryDetailPageArgs.mPackageName);
        }
        bundle.putInt("extra_uid", launchBatteryDetailPageArgs.mUid);
        bundle.putLong("extra_background_time", launchBatteryDetailPageArgs.mBackgroundTimeMs);
        bundle.putLong("extra_foreground_time", launchBatteryDetailPageArgs.mForegroundTimeMs);
        bundle.putString("extra_slot_time", launchBatteryDetailPageArgs.mSlotInformation);
        bundle.putString("extra_power_usage_percent", launchBatteryDetailPageArgs.mUsagePercent);
        bundle.putInt("extra_power_usage_amount", launchBatteryDetailPageArgs.mConsumedPower);
        new SubSettingLauncher(activity).setDestination(AdvancedPowerUsageDetail.class.getName()).setTitleRes(R$string.battery_details_title).setArguments(bundle).setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory()).setUserHandle(new UserHandle(launchBatteryDetailPageArgs.mIsUserEntry ? ActivityManager.getCurrentUser() : UserHandle.getUserId(launchBatteryDetailPageArgs.mUid))).launch();
    }

    public static void startBatteryDetailPage(Activity activity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, String str) {
        Bundle bundle = new Bundle(3);
        PackageManager packageManager = activity.getPackageManager();
        bundle.putString("extra_package_name", str);
        bundle.putString("extra_power_usage_percent", Utils.formatPercentage(0));
        try {
            bundle.putInt("extra_uid", packageManager.getPackageUid(str, 0));
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("AdvancedPowerDetail", "Cannot find package: " + str, e);
        }
        new SubSettingLauncher(activity).setDestination(AdvancedPowerUsageDetail.class.getName()).setTitleRes(R$string.battery_details_title).setArguments(bundle).setSourceMetricsCategory(instrumentedPreferenceFragment.getMetricsCategory()).launch();
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mState = ApplicationsState.getInstance(getActivity().getApplication());
        this.mBatteryUtils = BatteryUtils.getInstance(getContext());
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        String string = getArguments().getString("extra_package_name");
        if (this.mEnableTriState) {
            onCreateForTriState(string);
        } else {
            this.mForegroundPreference = findPreference("app_usage_foreground");
            this.mBackgroundPreference = findPreference("app_usage_background");
        }
        this.mHeaderPreference = (LayoutPreference) findPreference("header_view");
        if (string != null) {
            this.mAppEntry = this.mState.getEntry(string, UserHandle.myUserId());
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        initHeader();
        if (this.mEnableTriState) {
            this.mOptimizationMode = this.mBatteryOptimizeUtils.getAppOptimizationMode();
            initPreferenceForTriState(getContext());
            FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider().action(getContext(), 1889, this.mBatteryOptimizeUtils.getPackageName());
            return;
        }
        initPreference(getContext());
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        if (this.mEnableTriState) {
            int selectedPreference = getSelectedPreference();
            notifyBackupManager();
            logMetricCategory(selectedPreference);
            this.mBatteryOptimizeUtils.setAppUsageState(selectedPreference);
            Log.d("AdvancedPowerDetail", "Leave with mode: " + selectedPreference);
        }
    }

    void notifyBackupManager() {
        if (this.mOptimizationMode != this.mBatteryOptimizeUtils.getAppOptimizationMode()) {
            BackupManager backupManager = this.mBackupManager;
            if (backupManager == null) {
                backupManager = new BackupManager(getContext());
            }
            backupManager.dataChanged();
        }
    }

    void initHeader() {
        View findViewById = this.mHeaderPreference.findViewById(R$id.entity_header);
        FragmentActivity activity = getActivity();
        Bundle arguments = getArguments();
        EntityHeaderController buttonActions = EntityHeaderController.newInstance(activity, this, findViewById).setRecyclerView(getListView(), getSettingsLifecycle()).setButtonActions(0, 0);
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null) {
            buttonActions.setLabel(arguments.getString("extra_label"));
            if (arguments.getInt("extra_icon_id", 0) == 0) {
                buttonActions.setIcon(activity.getPackageManager().getDefaultActivityIcon());
            } else {
                buttonActions.setIcon(activity.getDrawable(arguments.getInt("extra_icon_id")));
            }
        } else {
            this.mState.ensureIcon(appEntry);
            buttonActions.setLabel(this.mAppEntry);
            buttonActions.setIcon(this.mAppEntry);
            buttonActions.setIsInstantApp(AppUtils.isInstant(this.mAppEntry.info));
        }
        if (this.mEnableTriState) {
            buttonActions.setSummary(getAppActiveTime(arguments));
        }
        buttonActions.done((Activity) activity, true);
    }

    void initPreference(Context context) {
        Bundle arguments = getArguments();
        long j = arguments.getLong("extra_foreground_time");
        long j2 = arguments.getLong("extra_background_time");
        this.mForegroundPreference.setSummary(TextUtils.expandTemplate(getText(R$string.battery_used_for), StringUtil.formatElapsedTime(context, j, false, false)));
        this.mBackgroundPreference.setSummary(TextUtils.expandTemplate(getText(R$string.battery_active_for), StringUtil.formatElapsedTime(context, j2, false, false)));
    }

    void initPreferenceForTriState(Context context) {
        String string;
        if (!this.mBatteryOptimizeUtils.isValidPackageName()) {
            string = context.getString(R$string.manager_battery_usage_footer_limited, context.getString(R$string.manager_battery_usage_optimized_only));
        } else if (this.mBatteryOptimizeUtils.isSystemOrDefaultApp()) {
            string = context.getString(R$string.manager_battery_usage_footer_limited, context.getString(R$string.manager_battery_usage_unrestricted_only));
        } else {
            string = context.getString(R$string.manager_battery_usage_footer);
        }
        this.mFooterPreference.setTitle(string);
        final Intent helpIntent = HelpUtils.getHelpIntent(context, context.getString(R$string.help_url_app_usage_settings), "");
        if (helpIntent != null) {
            this.mFooterPreference.setLearnMoreAction(new View.OnClickListener() { // from class: com.android.settings.fuelgauge.AdvancedPowerUsageDetail$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdvancedPowerUsageDetail.this.lambda$initPreferenceForTriState$0(helpIntent, view);
                }
            });
            this.mFooterPreference.setLearnMoreText(context.getString(R$string.manager_battery_usage_link_a11y));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initPreferenceForTriState$0(Intent intent, View view) {
        startActivityForResult(intent, 0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return this.mEnableTriState ? R$xml.power_usage_detail : R$xml.power_usage_detail_legacy;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        ArrayList arrayList = new ArrayList();
        Bundle arguments = getArguments();
        int i = arguments.getInt("extra_uid", 0);
        String string = arguments.getString("extra_package_name");
        AppButtonsPreferenceController appButtonsPreferenceController = new AppButtonsPreferenceController((SettingsActivity) getActivity(), this, getSettingsLifecycle(), string, this.mState, 0, 1);
        this.mAppButtonsPreferenceController = appButtonsPreferenceController;
        arrayList.add(appButtonsPreferenceController);
        if (this.mEnableTriState) {
            arrayList.add(new UnrestrictedPreferenceController(context, i, string));
            arrayList.add(new OptimizedPreferenceController(context, i, string));
            arrayList.add(new RestrictedPreferenceController(context, i, string));
        } else {
            BackgroundActivityPreferenceController backgroundActivityPreferenceController = new BackgroundActivityPreferenceController(context, this, i, string);
            this.mBackgroundActivityPreferenceController = backgroundActivityPreferenceController;
            arrayList.add(backgroundActivityPreferenceController);
            arrayList.add(new BatteryOptimizationPreferenceController((SettingsActivity) getActivity(), this, string));
        }
        return arrayList;
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        AppButtonsPreferenceController appButtonsPreferenceController = this.mAppButtonsPreferenceController;
        if (appButtonsPreferenceController != null) {
            appButtonsPreferenceController.handleActivityResult(i, i2, intent);
        }
    }

    @Override // com.android.settings.applications.appinfo.ButtonActionDialogFragment.AppButtonsDialogListener
    public void handleDialogClick(int i) {
        AppButtonsPreferenceController appButtonsPreferenceController = this.mAppButtonsPreferenceController;
        if (appButtonsPreferenceController != null) {
            appButtonsPreferenceController.handleDialogClick(i);
        }
    }

    @Override // com.android.settings.fuelgauge.batterytip.BatteryTipPreferenceController.BatteryTipListener
    public void onBatteryTipHandled(BatteryTip batteryTip) {
        BackgroundActivityPreferenceController backgroundActivityPreferenceController = this.mBackgroundActivityPreferenceController;
        backgroundActivityPreferenceController.updateSummary(findPreference(backgroundActivityPreferenceController.getPreferenceKey()));
    }

    @Override // com.android.settingslib.widget.SelectorWithWidgetPreference.OnClickListener
    public void onRadioButtonClicked(SelectorWithWidgetPreference selectorWithWidgetPreference) {
        String key = selectorWithWidgetPreference.getKey();
        updatePreferenceState(this.mUnrestrictedPreference, key);
        updatePreferenceState(this.mOptimizePreference, key);
        updatePreferenceState(this.mRestrictedPreference, key);
    }

    private void updatePreferenceState(SelectorWithWidgetPreference selectorWithWidgetPreference, String str) {
        selectorWithWidgetPreference.setChecked(str.equals(selectorWithWidgetPreference.getKey()));
    }

    private void logMetricCategory(int i) {
        if (i == this.mOptimizationMode) {
            return;
        }
        int i2 = 0;
        if (i == 1) {
            i2 = 1778;
        } else if (i == 2) {
            i2 = 1776;
        } else if (i == 3) {
            i2 = 1777;
        }
        int i3 = i2;
        if (i3 != 0) {
            String packageName = this.mBatteryOptimizeUtils.getPackageName();
            MetricsFeatureProvider metricsFeatureProvider = FeatureFactory.getFactory(getContext()).getMetricsFeatureProvider();
            if (TextUtils.isEmpty(packageName)) {
                packageName = "none";
            }
            metricsFeatureProvider.action(1889, i3, 1889, packageName, getArguments().getInt("extra_power_usage_amount"));
        }
    }

    private void onCreateForTriState(String str) {
        this.mUnrestrictedPreference = (SelectorWithWidgetPreference) findPreference("unrestricted_pref");
        this.mOptimizePreference = (SelectorWithWidgetPreference) findPreference("optimized_pref");
        this.mRestrictedPreference = (SelectorWithWidgetPreference) findPreference("restricted_pref");
        this.mFooterPreference = (FooterPreference) findPreference("app_usage_footer_preference");
        this.mUnrestrictedPreference.setOnClickListener(this);
        this.mOptimizePreference.setOnClickListener(this);
        this.mRestrictedPreference.setOnClickListener(this);
        this.mBatteryOptimizeUtils = new BatteryOptimizeUtils(getContext(), getArguments().getInt("extra_uid"), str);
    }

    private int getSelectedPreference() {
        if (this.mRestrictedPreference.isChecked()) {
            return 1;
        }
        if (this.mUnrestrictedPreference.isChecked()) {
            return 2;
        }
        return this.mOptimizePreference.isChecked() ? 3 : 0;
    }

    private CharSequence getAppActiveTime(Bundle bundle) {
        long j = bundle.getLong("extra_foreground_time");
        long j2 = bundle.getLong("extra_background_time");
        int i = bundle.getInt("extra_power_usage_amount");
        int i2 = bundle.getInt("extra_uid", 0);
        String string = bundle.getString("extra_slot_time", null);
        long j3 = j + j2;
        boolean isChartGraphEnabled = FeatureFactory.getFactory(getContext()).getPowerUsageFeatureProvider(getContext()).isChartGraphEnabled(getContext());
        if (isChartGraphEnabled || !BatteryEntry.isSystemUid(i2)) {
            if (j3 == 0) {
                int i3 = i > 0 ? R$string.battery_usage_without_time : R$string.battery_not_usage_24hr;
                if (!isChartGraphEnabled) {
                    i3 = R$string.battery_not_usage;
                }
                return getText(i3);
            } else if (string == null) {
                if (isChartGraphEnabled) {
                    return getAppPast24HrActiveSummary(j, j2, j3);
                }
                return getAppFullChargeActiveSummary(j, j2, j3);
            } else {
                return getAppActiveSummaryWithSlotTime(j, j2, j3, string);
            }
        }
        return null;
    }

    private CharSequence getAppFullChargeActiveSummary(long j, long j2, long j3) {
        int i;
        if (j == 0 && j2 != 0) {
            if (j2 < 60000) {
                return getText(R$string.battery_bg_usage_less_minute);
            }
            return TextUtils.expandTemplate(getText(R$string.battery_bg_usage), StringUtil.formatElapsedTime(getContext(), j2, false, false));
        } else if (j3 < 60000) {
            return getText(R$string.battery_total_usage_less_minute);
        } else {
            if (j2 < 60000) {
                if (j2 == 0) {
                    i = R$string.battery_total_usage;
                } else {
                    i = R$string.battery_total_usage_and_bg_less_minute_usage;
                }
                return TextUtils.expandTemplate(getText(i), StringUtil.formatElapsedTime(getContext(), j3, false, false));
            }
            return TextUtils.expandTemplate(getText(R$string.battery_total_and_bg_usage), StringUtil.formatElapsedTime(getContext(), j3, false, false), StringUtil.formatElapsedTime(getContext(), j2, false, false));
        }
    }

    private CharSequence getAppPast24HrActiveSummary(long j, long j2, long j3) {
        int i;
        if (j == 0 && j2 != 0) {
            if (j2 < 60000) {
                return getText(R$string.battery_bg_usage_less_minute_24hr);
            }
            return TextUtils.expandTemplate(getText(R$string.battery_bg_usage_24hr), StringUtil.formatElapsedTime(getContext(), j2, false, false));
        } else if (j3 < 60000) {
            return getText(R$string.battery_total_usage_less_minute_24hr);
        } else {
            if (j2 < 60000) {
                if (j2 == 0) {
                    i = R$string.battery_total_usage_24hr;
                } else {
                    i = R$string.battery_total_usage_and_bg_less_minute_usage_24hr;
                }
                return TextUtils.expandTemplate(getText(i), StringUtil.formatElapsedTime(getContext(), j3, false, false));
            }
            return TextUtils.expandTemplate(getText(R$string.battery_total_and_bg_usage_24hr), StringUtil.formatElapsedTime(getContext(), j3, false, false), StringUtil.formatElapsedTime(getContext(), j2, false, false));
        }
    }

    private CharSequence getAppActiveSummaryWithSlotTime(long j, long j2, long j3, String str) {
        int i;
        if (j == 0 && j2 != 0) {
            if (j2 < 60000) {
                return TextUtils.expandTemplate(getText(R$string.battery_bg_usage_less_minute_with_period), str);
            }
            return TextUtils.expandTemplate(getText(R$string.battery_bg_usage_with_period), StringUtil.formatElapsedTime(getContext(), j2, false, false), str);
        } else if (j3 < 60000) {
            return TextUtils.expandTemplate(getText(R$string.battery_total_usage_less_minute_with_period), str);
        } else {
            if (j2 < 60000) {
                if (j2 == 0) {
                    i = R$string.battery_total_usage_with_period;
                } else {
                    i = R$string.battery_total_usage_and_bg_less_minute_usage_with_period;
                }
                return TextUtils.expandTemplate(getText(i), StringUtil.formatElapsedTime(getContext(), j3, false, false), str);
            }
            return TextUtils.expandTemplate(getText(R$string.battery_total_and_bg_usage_with_period), StringUtil.formatElapsedTime(getContext(), j3, false, false), StringUtil.formatElapsedTime(getContext(), j2, false, false), str);
        }
    }
}
