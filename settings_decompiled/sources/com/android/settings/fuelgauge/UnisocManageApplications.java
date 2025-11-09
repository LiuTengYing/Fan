package com.android.settings.fuelgauge;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.BatteryUsageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UidBatteryConsumer;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.sprdpower.IPowerManagerEx;
import android.preference.PreferenceFrameLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.AppStateBaseBridge;
import com.android.settings.applications.AppStatePowerBridge;
import com.android.settings.applications.manageapplications.ResetAppsHelper;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.fuelgauge.UnisocManageApplications;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.LoadingViewController;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public class UnisocManageApplications extends SettingsPreferenceFragment implements AdapterView.OnItemClickListener {
    public static final ApplicationsState.AppFilter[] FILTERS;
    private Switch mAllAppSwitch;
    private TextView mAllAppTitle;
    private int mAppConfigType;
    public ApplicationsAdapter mApplications;
    private ApplicationsState mApplicationsState;
    private int mArgument;
    private String mBatteryPercent;
    private BatteryUsageStats mBatteryUsageStats;
    private BatteryUtils mBatteryUtils;
    private View mEmptyView;
    private TextView mFeatureTitle;
    public int mFilter;
    private LayoutInflater mInflater;
    private ListView mListView;
    private View mLoadingContainer;
    private PackageManager mPackageManager;
    private PowerManager mPowerManager;
    private IPowerManagerEx mPowerManagerEx;
    private ResetAppsHelper mResetAppsHelper;
    private View mRootView;
    private boolean mShowSystem;
    private View mSwitchButton;
    private View mSwitchContainer;
    private UiModeManager mUiModeManager;
    private UidBatteryConsumer mUidBatteryConsumer;
    private List<UidBatteryConsumer> mUsageList;
    private UserManager mUserManager;
    private int muid;
    private int mSortOrder = R$id.sort_order_alpha;
    public int mListType = 0;
    private boolean mIsSelectAll = false;
    private int mSwitchState = 0;
    private LoaderManager.LoaderCallbacks<BatteryUsageStats> mBatteryCallbacks = new LoaderManager.LoaderCallbacks<BatteryUsageStats>() { // from class: com.android.settings.fuelgauge.UnisocManageApplications.1
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<BatteryUsageStats> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<BatteryUsageStats> onCreateLoader(int i, Bundle bundle) {
            return new BatteryUsageStatsLoader(UnisocManageApplications.this.getContext(), true);
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<BatteryUsageStats> loader, BatteryUsageStats batteryUsageStats) {
            UnisocManageApplications.this.mBatteryUsageStats = batteryUsageStats;
            if (UnisocManageApplications.this.mBatteryUsageStats != null) {
                UnisocManageApplications.this.mUsageList = new ArrayList(UnisocManageApplications.this.mBatteryUsageStats.getUidBatteryConsumers());
            }
        }
    };
    public Comparator<ApplicationsState.AppEntry> APP_STATE_COMPARATOR = new Comparator<ApplicationsState.AppEntry>() { // from class: com.android.settings.fuelgauge.UnisocManageApplications.3
        @Override // java.util.Comparator
        public int compare(ApplicationsState.AppEntry appEntry, ApplicationsState.AppEntry appEntry2) {
            try {
                int appPowerSaveConfigWithType = UnisocManageApplications.this.mPowerManagerEx.getAppPowerSaveConfigWithType(appEntry.info.packageName, UnisocManageApplications.this.mAppConfigType);
                int appPowerSaveConfigWithType2 = UnisocManageApplications.this.mPowerManagerEx.getAppPowerSaveConfigWithType(appEntry2.info.packageName, UnisocManageApplications.this.mAppConfigType);
                return appPowerSaveConfigWithType2 == appPowerSaveConfigWithType ? ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2) : appPowerSaveConfigWithType - appPowerSaveConfigWithType2;
            } catch (RemoteException unused) {
                return ApplicationsState.ALPHA_COMPARATOR.compare(appEntry, appEntry2);
            }
        }
    };

    static {
        ApplicationsState.AppFilter appFilter = AppStatePowerBridge.FILTER_POWER_ALLOWLISTED;
        ApplicationsState.AppFilter appFilter2 = ApplicationsState.FILTER_ALL_ENABLED;
        FILTERS = new ApplicationsState.AppFilter[]{new ApplicationsState.CompoundFilter(appFilter, appFilter2), new ApplicationsState.CompoundFilter(ApplicationsState.FILTER_WITHOUT_DISABLED_UNTIL_USED, appFilter2), ApplicationsState.UNISOC_FILTER_THIRD_PARTY, ApplicationsState.FILTER_EVERYTHING, appFilter2};
    }

    /* loaded from: classes.dex */
    public enum ConfigType {
        TYPE_NULL(-1),
        TYPE_OPTIMIZE(0),
        TYPE_ALARM(1),
        TYPE_WAKELOCK(2),
        TYPE_NETWORK(3),
        TYPE_AUTOLAUNCH(4),
        TYPE_SECONDARYLAUNCH(5),
        TYPE_LOCKSCREENCLEANUP(6),
        TYPE_POWERCONSUMERTYPE(7),
        TYPE_MAX(8);
        
        public final int value;

        ConfigType(int i) {
            this.value = i;
        }
    }

    private void startAdvancedPowerUsageDetail(String str, int i) {
        this.mUidBatteryConsumer = findTargetUidBatteryConsumer(this.mBatteryUsageStats, this.muid);
        if (isBatteryStatsAvailable()) {
            updateBatteryPercent();
            String[] packagesForUid = this.mPackageManager.getPackagesForUid(this.mUidBatteryConsumer.getUid());
            boolean shouldHideUidBatteryConsumer = this.mBatteryUtils.shouldHideUidBatteryConsumer(this.mUidBatteryConsumer, packagesForUid);
            Context context = getContext();
            UserManager userManager = this.mUserManager;
            UidBatteryConsumer uidBatteryConsumer = this.mUidBatteryConsumer;
            AdvancedPowerUsageDetail.startBatteryDetailPage((SettingsActivity) getActivity(), this, new BatteryEntry(context, null, userManager, uidBatteryConsumer, shouldHideUidBatteryConsumer, uidBatteryConsumer.getUid(), packagesForUid, str), this.mBatteryPercent, !FeatureFactory.getFactory(getContext()).getPowerUsageFeatureProvider(getContext()).isChartGraphEnabled(getContext()));
            return;
        }
        AdvancedPowerUsageDetail.startBatteryDetailPage((SettingsActivity) getActivity(), this, str);
    }

    private boolean isBatteryStatsAvailable() {
        return (this.mBatteryUsageStats == null || this.mUidBatteryConsumer == null) ? false : true;
    }

    private UidBatteryConsumer findTargetUidBatteryConsumer(BatteryUsageStats batteryUsageStats, int i) {
        List<UidBatteryConsumer> list = this.mUsageList;
        if (list != null) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                UidBatteryConsumer uidBatteryConsumer = this.mUsageList.get(i2);
                if (uidBatteryConsumer.getUid() == i) {
                    return uidBatteryConsumer;
                }
            }
            return null;
        }
        return null;
    }

    public void updateBatteryPercent() {
        if (isBatteryStatsAvailable()) {
            this.mBatteryPercent = Utils.formatPercentage((int) this.mBatteryUtils.calculateBatteryPercent(this.mUidBatteryConsumer.getConsumedPower(), this.mBatteryUsageStats.getConsumedPower(), this.mBatteryUsageStats.getDischargePercentage()));
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        this.mApplicationsState = ApplicationsState.getInstance(getActivity().getApplication());
        Bundle arguments = getArguments();
        this.mArgument = arguments != null ? arguments.getInt("type_app_config") : 1005;
        this.mAppConfigType = getCurrentType();
        Log.d("UnisocManageApplications", " mArgument = " + this.mArgument + " mAppConfigType = " + this.mAppConfigType);
        this.mFilter = 2;
        if (bundle != null) {
            this.mSortOrder = bundle.getInt("sortOrder", this.mSortOrder);
            this.mShowSystem = bundle.getBoolean("showSystem", this.mShowSystem);
        }
        this.mResetAppsHelper = new ResetAppsHelper(getActivity());
        this.mPowerManagerEx = IPowerManagerEx.Stub.asInterface(ServiceManager.getService("power_ex"));
        FragmentActivity activity = getActivity();
        activity.setRequestedOrientation(1);
        this.mBatteryUtils = BatteryUtils.getInstance(getContext());
        this.mUserManager = (UserManager) activity.getSystemService("user");
        this.mPackageManager = activity.getPackageManager();
        this.mUiModeManager = (UiModeManager) activity.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) activity.getSystemService(PowerManager.class);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        this.mInflater = layoutInflater;
        View inflate = layoutInflater.inflate(R$layout.unisoc_manage_applications_apps, (ViewGroup) null);
        this.mRootView = inflate;
        this.mLoadingContainer = inflate.findViewById(R$id.loading_container);
        this.mEmptyView = this.mRootView.findViewById(16908292);
        this.mListView = (ListView) this.mRootView.findViewById(16908298);
        this.mSwitchContainer = this.mRootView.findViewById(R$id.switch_container);
        this.mSwitchButton = this.mRootView.findViewById(R$id.switch_button);
        this.mFeatureTitle = (TextView) this.mRootView.findViewById(R$id.feature_title);
        this.mAllAppTitle = (TextView) this.mRootView.findViewById(16908310);
        this.mAllAppSwitch = (Switch) this.mRootView.findViewById(R$id.security_toggle_all);
        updateFeatureTitleBackgoundColor();
        int i = this.mArgument;
        if (i == 1000) {
            this.mFeatureTitle.setVisibility(8);
            this.mSwitchContainer.setVisibility(8);
        } else if (i == 1003) {
            this.mFeatureTitle.setText(R$string.auto_launch_title);
            this.mAllAppTitle.setText(R$string.not_allow_all_app);
        } else if (i == 1004) {
            this.mFeatureTitle.setText(R$string.second_launch_title);
            this.mAllAppTitle.setText(R$string.not_allow_all_app);
        } else if (i == 1006) {
            this.mFeatureTitle.setText(R$string.power_intensive_title);
            this.mSwitchContainer.setVisibility(8);
        }
        View view = this.mSwitchButton;
        if (view != null) {
            view.setEnabled(false);
            this.mSwitchButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.fuelgauge.UnisocManageApplications.2
                @Override // android.view.View.OnClickListener
                public void onClick(View view2) {
                    if (UnisocManageApplications.this.mAllAppSwitch != null) {
                        boolean isChecked = UnisocManageApplications.this.mAllAppSwitch.isChecked();
                        UnisocManageApplications.this.mIsSelectAll = !isChecked;
                        Log.d("UnisocManageApplications", "onClick() isChecked = " + isChecked + " mIsSelectAll = " + UnisocManageApplications.this.mIsSelectAll);
                        UnisocManageApplications.this.mAllAppSwitch.setChecked(UnisocManageApplications.this.mIsSelectAll);
                        UnisocManageApplications unisocManageApplications = UnisocManageApplications.this;
                        unisocManageApplications.setAllAppState(unisocManageApplications.mIsSelectAll);
                    }
                }
            });
        }
        this.mListView.setOnItemClickListener(this);
        this.mListView.setSaveEnabled(true);
        this.mListView.setItemsCanFocus(true);
        this.mListView.setTextFilterEnabled(true);
        ApplicationsAdapter applicationsAdapter = new ApplicationsAdapter(this.mApplicationsState, this, this.mFilter);
        this.mApplications = applicationsAdapter;
        if (bundle != null) {
            applicationsAdapter.mHasReceivedBridgeCallback = bundle.getBoolean("hasBridge", false);
        }
        this.mListView.setAdapter((ListAdapter) this.mApplications);
        this.mListView.setRecyclerListener(this.mApplications);
        this.mListView.setVerticalScrollBarEnabled(false);
        com.android.settings.Utils.prepareCustomPreferencesList(viewGroup, this.mRootView, this.mListView, false);
        if (viewGroup instanceof PreferenceFrameLayout) {
            this.mRootView.getLayoutParams().removeBorders = true;
        }
        this.mResetAppsHelper.onRestoreInstanceState(bundle);
        return this.mRootView;
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    private void updateFeatureTitleBackgoundColor() {
        int nightMode = this.mUiModeManager.getNightMode();
        if (this.mFeatureTitle != null) {
            if (nightMode == 2 || isPowerSaveMode()) {
                this.mFeatureTitle.setBackgroundColor(Color.parseColor("#8a000000"));
            } else {
                this.mFeatureTitle.setBackgroundColor(Color.parseColor("#E1E1E1"));
            }
        }
    }

    boolean isPowerSaveMode() {
        return this.mPowerManager.isPowerSaveMode();
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return this.mListType != 0 ? 0 : 65;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        updateView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.resume(this.mSortOrder);
            this.mApplications.updateLoading();
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(1, Bundle.EMPTY, this.mBatteryCallbacks);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        getLoaderManager().destroyLoader(1);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this.mResetAppsHelper.onSaveInstanceState(bundle);
        bundle.putInt("sortOrder", this.mSortOrder);
        bundle.putBoolean("showSystem", this.mShowSystem);
        bundle.putBoolean("hasBridge", this.mApplications.mHasReceivedBridgeCallback);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.pause();
        }
        this.mResetAppsHelper.stop();
    }

    @Override // androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onDestroyView() {
        super.onDestroyView();
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter != null) {
            applicationsAdapter.release();
        }
        this.mRootView = null;
    }

    public int getCurrentType() {
        switch (this.mArgument) {
            case 1003:
                return ConfigType.TYPE_AUTOLAUNCH.value;
            case 1004:
                return ConfigType.TYPE_SECONDARYLAUNCH.value;
            case 1005:
                return ConfigType.TYPE_LOCKSCREENCLEANUP.value;
            default:
                return ConfigType.TYPE_AUTOLAUNCH.value;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v1 */
    /* JADX WARN: Type inference failed for: r5v2 */
    /* JADX WARN: Type inference failed for: r5v3, types: [int] */
    /* JADX WARN: Type inference failed for: r5v4 */
    public void setAllAppState(boolean z) {
        Log.i("UnisocManageApplications", "closeAllChanged flag:" + z);
        if (this.mIsSelectAll) {
            this.mSwitchState = 2;
        } else {
            this.mSwitchState = 1;
        }
        try {
            this.mPowerManagerEx.setAppPowerSaveConfigListWithType(this.mApplications.mPkgList, this.mAppConfigType, (int) (this.mArgument == 1005 ? z : z ? 2 : 1));
        } catch (RemoteException unused) {
        }
        this.mApplications.notifyDataSetChanged();
    }

    public void updateView() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            activity.invalidateOptionsMenu();
            Log.d("UnisocManageApplications", "updateView() mode = " + activity.isInMultiWindowMode());
            if (activity.isInMultiWindowMode()) {
                this.mFeatureTitle.setVisibility(8);
            } else if (this.mArgument == 1000) {
                this.mFeatureTitle.setVisibility(8);
            } else {
                this.mFeatureTitle.setVisibility(0);
            }
        }
    }

    private void startAppConfigFragment(Class<?> cls, String str, String str2) {
        Bundle bundle = new Bundle();
        bundle.putString("package", str);
        new SubSettingLauncher(getActivity()).setDestination(cls.getName()).setArguments(bundle).setTitleText(str2).setSourceMetricsCategory(getMetricsCategory()).launch();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v5 */
    /* JADX WARN: Type inference failed for: r7v6 */
    /* JADX WARN: Type inference failed for: r7v7, types: [int] */
    /* JADX WARN: Type inference failed for: r7v8 */
    @Override // android.widget.AdapterView.OnItemClickListener
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
        ApplicationsAdapter applicationsAdapter = this.mApplications;
        if (applicationsAdapter == null || applicationsAdapter.getCount() <= i) {
            return;
        }
        ApplicationsState.AppEntry appEntry = this.mApplications.getAppEntry(i);
        ApplicationInfo applicationInfo = appEntry.info;
        String str = applicationInfo.packageName;
        int i2 = applicationInfo.uid;
        this.muid = i2;
        int i3 = this.mArgument;
        if (i3 == 1000) {
            startAppConfigFragment(AppItemBatterySaverFragment.class, str, appEntry.label);
        } else if (i3 == 1006) {
            startAdvancedPowerUsageDetail(str, i2);
        } else {
            UnisocAppViewHolder unisocAppViewHolder = (UnisocAppViewHolder) view.getTag();
            if (unisocAppViewHolder == null) {
                return;
            }
            unisocAppViewHolder.app_switch.toggle();
            boolean isChecked = unisocAppViewHolder.app_switch.isChecked();
            Log.d("UnisocManageApplications", "isChecked: " + isChecked + " pkg: " + str);
            this.mSwitchState = 0;
            try {
                this.mPowerManagerEx.setAppPowerSaveConfigWithType(str, this.mAppConfigType, (int) (this.mArgument == 1005 ? isChecked : isChecked ? 2 : 1));
                this.mApplications.setAppStateText(isChecked, unisocAppViewHolder);
            } catch (RemoteException unused) {
            }
            if (isChecked) {
                this.mApplications.setSwitchIfAllAppOpen();
                return;
            }
            Switch r2 = this.mAllAppSwitch;
            if (r2 != null) {
                r2.setChecked(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ApplicationsAdapter extends BaseAdapter implements ApplicationsState.Callbacks, AppStateBaseBridge.Callback, AbsListView.RecyclerListener {
        private ArrayList<ApplicationsState.AppEntry> mBaseEntries;
        private final Handler mBgHandler;
        private final Context mContext;
        private ArrayList<ApplicationsState.AppEntry> mEntries;
        private int mFilterMode;
        private boolean mHasReceivedBridgeCallback;
        private boolean mHasReceivedLoadEntries;
        private final LoadingViewController mLoadingViewController;
        private final UnisocManageApplications mManageApplications;
        private ArrayList<String> mPkgList;
        private boolean mResumed;
        private final ApplicationsState.Session mSession;
        private final ApplicationsState mState;
        private final ArrayList<View> mActive = new ArrayList<>();
        private final AppStateBaseBridge mExtraInfoBridge = null;
        private int mLastSortMode = -1;
        private final Handler mFgHandler = new Handler();

        @Override // android.widget.BaseAdapter, android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageIconChanged() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageSizeChanged(String str) {
        }

        public ApplicationsAdapter(ApplicationsState applicationsState, UnisocManageApplications unisocManageApplications, int i) {
            this.mState = applicationsState;
            this.mBgHandler = new Handler(applicationsState.getBackgroundLooper());
            this.mSession = applicationsState.newSession(this);
            this.mManageApplications = unisocManageApplications;
            this.mContext = unisocManageApplications.getActivity();
            this.mFilterMode = i;
            this.mLoadingViewController = new LoadingViewController(unisocManageApplications.mLoadingContainer, unisocManageApplications.mListView, unisocManageApplications.mEmptyView);
        }

        public void resume(int i) {
            Log.i("UnisocManageApplications", "Resume!  mResumed=" + this.mResumed);
            if (!this.mResumed) {
                this.mResumed = true;
                this.mSession.onResume();
                this.mLastSortMode = i;
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.resume(false);
                }
                rebuild(false);
                return;
            }
            rebuild(i);
        }

        public void pause() {
            if (this.mResumed) {
                this.mResumed = false;
                this.mSession.onPause();
                AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
                if (appStateBaseBridge != null) {
                    appStateBaseBridge.pause();
                }
            }
        }

        public void release() {
            this.mSession.onDestroy();
            AppStateBaseBridge appStateBaseBridge = this.mExtraInfoBridge;
            if (appStateBaseBridge != null) {
                appStateBaseBridge.release();
            }
        }

        public void rebuild(int i) {
            if (i == this.mLastSortMode) {
                return;
            }
            this.mLastSortMode = i;
            rebuild(true);
        }

        public void rebuild(boolean z) {
            if (this.mHasReceivedLoadEntries) {
                if (this.mExtraInfoBridge == null || this.mHasReceivedBridgeCallback) {
                    Log.i("UnisocManageApplications", "Rebuilding app list...mFilterMode = " + this.mFilterMode + " mShowSystem = " + this.mManageApplications.mShowSystem);
                    final ApplicationsState.AppFilter appFilter = UnisocManageApplications.FILTERS[this.mFilterMode];
                    final Comparator<ApplicationsState.AppEntry> comparator = this.mManageApplications.APP_STATE_COMPARATOR;
                    this.mBgHandler.post(new Runnable() { // from class: com.android.settings.fuelgauge.UnisocManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            UnisocManageApplications.ApplicationsAdapter.this.lambda$rebuild$1(appFilter, comparator);
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$rebuild$1(ApplicationsState.AppFilter appFilter, Comparator comparator) {
            final ArrayList<ApplicationsState.AppEntry> rebuild = this.mSession.rebuild(appFilter, comparator, false);
            if (rebuild != null) {
                this.mFgHandler.post(new Runnable() { // from class: com.android.settings.fuelgauge.UnisocManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        UnisocManageApplications.ApplicationsAdapter.this.lambda$rebuild$0(rebuild);
                    }
                });
            }
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        /* renamed from: onRebuildComplete */
        public void lambda$rebuild$0(ArrayList<ApplicationsState.AppEntry> arrayList) {
            this.mBaseEntries = arrayList;
            this.mPkgList = new ArrayList<>();
            if (this.mBaseEntries != null) {
                if (this.mManageApplications.mArgument == 1006) {
                    this.mEntries = powerIntensiveFilter(this.mBaseEntries);
                } else {
                    this.mEntries = applyPrefixFilter(this.mBaseEntries);
                }
            } else {
                this.mEntries = null;
            }
            this.mManageApplications.mSwitchButton.setEnabled(true);
            notifyDataSetChanged();
            ArrayList<ApplicationsState.AppEntry> arrayList2 = this.mEntries;
            if (arrayList2 != null) {
                if (arrayList2.size() == 0) {
                    this.mLoadingViewController.showEmpty(false);
                } else {
                    this.mLoadingViewController.showContent(false);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateLoading() {
            if (this.mHasReceivedLoadEntries && this.mSession.getAllApps().size() != 0) {
                this.mLoadingViewController.showContent(false);
            } else {
                this.mLoadingViewController.showLoadingViewDelayed();
            }
        }

        ArrayList<ApplicationsState.AppEntry> applyPrefixFilter(ArrayList<ApplicationsState.AppEntry> arrayList) {
            ArrayList<ApplicationsState.AppEntry> arrayList2 = new ArrayList<>();
            int i = this.mManageApplications.mAppConfigType;
            int i2 = 0;
            boolean z = true;
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                ApplicationsState.AppEntry appEntry = arrayList.get(i3);
                boolean z2 = UserHandle.getUserId(appEntry.info.uid) == 0;
                if ((appEntry.info.flags & 8388608) != 0 && z2) {
                    arrayList2.add(appEntry);
                    if (this.mManageApplications.mArgument != 1000 && this.mManageApplications.mArgument != 1006) {
                        this.mPkgList.add(appEntry.info.packageName);
                        if (z) {
                            try {
                                i2 = this.mManageApplications.mPowerManagerEx.getAppPowerSaveConfigWithType(appEntry.info.packageName, i);
                            } catch (RemoteException unused) {
                            }
                            z &= this.mManageApplications.mArgument != 1005 ? i2 == 2 : i2 == 1;
                        }
                        Log.d("UnisocManageApplications", "pkg = " + appEntry.info + " isOwerApp = " + z2 + " isSelected = " + z);
                    }
                }
            }
            if (this.mManageApplications.mAllAppSwitch != null) {
                this.mManageApplications.mAllAppSwitch.setChecked(z);
            }
            return arrayList2;
        }

        ArrayList<ApplicationsState.AppEntry> powerIntensiveFilter(ArrayList<ApplicationsState.AppEntry> arrayList) {
            ArrayList<ApplicationsState.AppEntry> arrayList2 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                ApplicationsState.AppEntry appEntry = arrayList.get(i);
                boolean z = UserHandle.getUserId(appEntry.info.uid) == 0;
                int i2 = -1;
                try {
                    i2 = this.mManageApplications.mPowerManagerEx.getAppPowerSaveConfigWithType(appEntry.info.packageName, ConfigType.TYPE_POWERCONSUMERTYPE.value);
                } catch (RemoteException unused) {
                }
                if ((appEntry.info.flags & 8388608) != 0 && z && i2 > 0) {
                    arrayList2.add(appEntry);
                }
            }
            return arrayList2;
        }

        public void setSwitchIfAllAppOpen() {
            int i;
            boolean z = true;
            for (int i2 = 0; i2 < this.mEntries.size(); i2++) {
                try {
                    i = this.mManageApplications.mPowerManagerEx.getAppPowerSaveConfigWithType(this.mEntries.get(i2).info.packageName, this.mManageApplications.mAppConfigType);
                } catch (RemoteException unused) {
                    i = 0;
                }
                Log.i("UnisocManageApplications", "appPowerConfig = " + i);
                z &= this.mManageApplications.mArgument != 1005 ? i == 2 : i == 1;
                if (!z) {
                    break;
                }
            }
            if (this.mManageApplications.mAllAppSwitch != null) {
                this.mManageApplications.mAllAppSwitch.setChecked(z);
            }
        }

        @Override // com.android.settings.applications.AppStateBaseBridge.Callback
        public void onExtraInfoUpdated() {
            this.mHasReceivedBridgeCallback = true;
            rebuild(false);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onRunningStateChanged(boolean z) {
            this.mManageApplications.getActivity().setProgressBarIndeterminateVisibility(z);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onPackageListChanged() {
            Log.d("UnisocManageApplications", "onPackageListChanged()");
            this.mManageApplications.mSwitchState = 0;
            rebuild(false);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLoadEntriesCompleted() {
            this.mHasReceivedLoadEntries = true;
            rebuild(false);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onLauncherInfoChanged() {
            if (this.mManageApplications.mShowSystem) {
                return;
            }
            rebuild(false);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
        public void onAllSizesComputed() {
            if (this.mLastSortMode == R$id.sort_order_size) {
                rebuild(false);
            }
        }

        @Override // android.widget.Adapter
        public int getCount() {
            ArrayList<ApplicationsState.AppEntry> arrayList = this.mEntries;
            int size = arrayList != null ? arrayList.size() : 0;
            if (this.mManageApplications.mArgument != 1000 && this.mManageApplications.mArgument != 1006 && this.mManageApplications.mAllAppTitle != null && this.mManageApplications.mSwitchContainer != null) {
                if (size > 1) {
                    this.mManageApplications.mAllAppTitle.setVisibility(0);
                    this.mManageApplications.mSwitchContainer.setVisibility(0);
                } else {
                    this.mManageApplications.mAllAppTitle.setVisibility(8);
                    this.mManageApplications.mSwitchContainer.setVisibility(8);
                }
            }
            return size;
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            return this.mEntries.get(i);
        }

        public ApplicationsState.AppEntry getAppEntry(int i) {
            return this.mEntries.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return this.mEntries.get(i).id;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            final UnisocAppViewHolder createForCloseApp;
            int i2;
            if (this.mManageApplications.mArgument == 1000) {
                createForCloseApp = UnisocAppViewHolder.createOrRecycle(this.mManageApplications.mInflater, view);
            } else if (this.mManageApplications.mArgument == 1006) {
                createForCloseApp = UnisocAppViewHolder.createForPowerIntensiveApps(this.mManageApplications.mInflater, view);
            } else {
                createForCloseApp = UnisocAppViewHolder.createForCloseApp(this.mManageApplications.mInflater, view);
            }
            View view2 = createForCloseApp.rootView;
            final ApplicationsState.AppEntry appEntry = this.mEntries.get(i);
            boolean z = true;
            if (this.mManageApplications.mAllAppSwitch != null) {
                this.mManageApplications.mAllAppSwitch.setEnabled(true);
            }
            Log.d("UnisocManageApplications", "mSwitchState = " + this.mManageApplications.mSwitchState);
            synchronized (appEntry) {
                String str = appEntry.label;
                if (str != null) {
                    createForCloseApp.appName.setText(str);
                }
                Drawable iconFromCache = AppUtils.getIconFromCache(appEntry);
                if (iconFromCache != null && appEntry.mounted) {
                    createForCloseApp.appIcon.setImageDrawable(iconFromCache);
                } else {
                    ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.fuelgauge.UnisocManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            UnisocManageApplications.ApplicationsAdapter.this.lambda$getView$3(appEntry, createForCloseApp);
                        }
                    });
                }
                int i3 = this.mManageApplications.mAppConfigType;
                int i4 = -1;
                try {
                    i2 = this.mManageApplications.mPowerManagerEx.getAppPowerSaveConfigWithType(appEntry.info.packageName, ConfigType.TYPE_POWERCONSUMERTYPE.value);
                    try {
                        if (i2 > 0) {
                            createForCloseApp.high_usage.setVisibility(0);
                            if (this.mManageApplications.mArgument == 1006) {
                                switch (i2) {
                                    case 1:
                                    case 3:
                                    case 5:
                                    case 7:
                                        createForCloseApp.high_usage_type.setText(R$string.power_consumer_type_alarm);
                                        break;
                                    case 2:
                                    case 6:
                                        createForCloseApp.high_usage_type.setText(R$string.power_consumer_type_wakelock);
                                        break;
                                    case 4:
                                        createForCloseApp.high_usage_type.setText(R$string.power_consumer_type_wakelock);
                                        break;
                                }
                            }
                        } else {
                            createForCloseApp.high_usage.setVisibility(8);
                        }
                    } catch (RemoteException unused) {
                    }
                } catch (RemoteException unused2) {
                    i2 = -1;
                }
                if (this.mManageApplications.mArgument != 1000 && this.mManageApplications.mArgument != 1006) {
                    if (this.mManageApplications.mSwitchState == 2) {
                        createForCloseApp.app_switch.setChecked(true);
                        if (this.mManageApplications.mArgument == 1005) {
                            createForCloseApp.appState.setText(R$string.close);
                        } else {
                            createForCloseApp.appState.setText(R$string.app_allow);
                        }
                    } else if (this.mManageApplications.mSwitchState == 1) {
                        createForCloseApp.app_switch.setChecked(false);
                        if (this.mManageApplications.mArgument == 1005) {
                            createForCloseApp.appState.setText(R$string.do_not_close);
                        } else {
                            createForCloseApp.appState.setText(R$string.app_not_allow);
                        }
                    } else if (this.mManageApplications.mSwitchState == 0) {
                        try {
                            i4 = this.mManageApplications.mPowerManagerEx.getAppPowerSaveConfigWithType(appEntry.info.packageName, i3);
                        } catch (RemoteException unused3) {
                        }
                        if (this.mManageApplications.mArgument == 1005) {
                            createForCloseApp.app_switch.setChecked(i4 == 1);
                            if (i4 != 1) {
                                z = false;
                            }
                            setAppStateText(z, createForCloseApp);
                        } else {
                            createForCloseApp.app_switch.setChecked(i4 == 2);
                            if (i4 != 2) {
                                z = false;
                            }
                            setAppStateText(z, createForCloseApp);
                        }
                        Log.d("UnisocManageApplications", "position: " + i + " currnetAppType: " + i3 + " appPowerConfig: " + i4 + " appPowerComsumerType:" + i2 + " pkg: " + appEntry.info.packageName);
                    }
                    this.mActive.remove(view2);
                    this.mActive.add(view2);
                    return view2;
                }
                return view2;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getView$3(ApplicationsState.AppEntry appEntry, final UnisocAppViewHolder unisocAppViewHolder) {
            final Drawable icon = AppUtils.getIcon(this.mContext, appEntry);
            if (icon != null) {
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.fuelgauge.UnisocManageApplications$ApplicationsAdapter$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        UnisocManageApplications.ApplicationsAdapter.lambda$getView$2(UnisocAppViewHolder.this, icon);
                    }
                });
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static /* synthetic */ void lambda$getView$2(UnisocAppViewHolder unisocAppViewHolder, Drawable drawable) {
            unisocAppViewHolder.appIcon.setImageDrawable(drawable);
        }

        @Override // android.widget.AbsListView.RecyclerListener
        public void onMovedToScrapHeap(View view) {
            this.mActive.remove(view);
        }

        public void setAppStateText(boolean z, UnisocAppViewHolder unisocAppViewHolder) {
            if (this.mManageApplications.mArgument == 1005) {
                if (z) {
                    unisocAppViewHolder.appState.setText(R$string.close);
                } else {
                    unisocAppViewHolder.appState.setText(R$string.do_not_close);
                }
            } else if (z) {
                unisocAppViewHolder.appState.setText(R$string.app_allow);
            } else {
                unisocAppViewHolder.appState.setText(R$string.app_not_allow);
            }
        }
    }
}
