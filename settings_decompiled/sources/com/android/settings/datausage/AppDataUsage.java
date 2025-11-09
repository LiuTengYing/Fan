package com.android.settings.datausage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import android.telephony.SubscriptionManager;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.view.View;
import android.widget.AdapterView;
import androidx.fragment.app.FragmentActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.android.settings.R$xml;
import com.android.settings.datausage.DataSaverBackend;
import com.android.settingslib.AppItem;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.net.NetworkCycleDataForUid;
import com.android.settingslib.net.NetworkCycleDataForUidLoader;
import com.android.settingslib.net.UidDetail;
import com.android.settingslib.net.UidDetailProvider;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class AppDataUsage extends DataUsageBaseFragment implements Preference.OnPreferenceChangeListener, DataSaverBackend.Listener {
    private AppItem mAppItem;
    private PreferenceCategory mAppList;
    private Preference mAppSettings;
    private Intent mAppSettingsIntent;
    private Preference mBackgroundUsage;
    private Context mContext;
    private SpinnerPreference mCycle;
    private CycleAdapter mCycleAdapter;
    private ArrayList<Long> mCycles;
    private DataSaverBackend mDataSaverBackend;
    private Preference mForegroundUsage;
    private Drawable mIcon;
    private boolean mIsLoading;
    CharSequence mLabel;
    private PackageManager mPackageManager;
    String mPackageName;
    private RestrictedSwitchPreference mRestrictBackground;
    private long mSelectedCycle;
    NetworkTemplate mTemplate;
    private Preference mTotalUsage;
    private RestrictedSwitchPreference mUnrestrictedData;
    private List<NetworkCycleDataForUid> mUsageData;
    private final ArraySet<String> mPackages = new ArraySet<>();
    private AdapterView.OnItemSelectedListener mCycleListener = new AdapterView.OnItemSelectedListener() { // from class: com.android.settings.datausage.AppDataUsage.1
        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
            AppDataUsage.this.bindData(i);
        }
    };
    final LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>> mUidDataCallbacks = new LoaderManager.LoaderCallbacks<List<NetworkCycleDataForUid>>() { // from class: com.android.settings.datausage.AppDataUsage.2
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<List<NetworkCycleDataForUid>> loader) {
        }

        /* JADX WARN: Type inference failed for: r1v1, types: [androidx.loader.content.Loader<java.util.List<com.android.settingslib.net.NetworkCycleDataForUid>>, com.android.settingslib.net.NetworkCycleDataLoader] */
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<List<NetworkCycleDataForUid>> onCreateLoader(int i, Bundle bundle) {
            NetworkCycleDataForUidLoader.Builder<?> builder = NetworkCycleDataForUidLoader.builder(AppDataUsage.this.mContext);
            builder.setRetrieveDetail(true).setNetworkTemplate(AppDataUsage.this.mTemplate);
            for (int i2 = 0; i2 < AppDataUsage.this.mAppItem.uids.size(); i2++) {
                builder.addUid(AppDataUsage.this.mAppItem.uids.keyAt(i2));
            }
            if (AppDataUsage.this.mCycles != null) {
                builder.setCycles(AppDataUsage.this.mCycles);
            }
            return builder.build();
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<List<NetworkCycleDataForUid>> loader, List<NetworkCycleDataForUid> list) {
            AppDataUsage.this.mUsageData = list;
            AppDataUsage.this.mCycleAdapter.updateCycleList(list);
            if (AppDataUsage.this.mSelectedCycle > 0) {
                int size = list.size();
                int i = 0;
                while (true) {
                    if (i >= size) {
                        i = 0;
                        break;
                    } else if (list.get(i).getEndTime() == AppDataUsage.this.mSelectedCycle) {
                        break;
                    } else {
                        i++;
                    }
                }
                if (i > 0) {
                    AppDataUsage.this.mCycle.setSelection(i);
                }
                AppDataUsage.this.bindData(i);
            } else {
                AppDataUsage.this.bindData(0);
            }
            AppDataUsage.this.mIsLoading = false;
        }
    };
    private final LoaderManager.LoaderCallbacks<ArraySet<Preference>> mAppPrefCallbacks = new LoaderManager.LoaderCallbacks<ArraySet<Preference>>() { // from class: com.android.settings.datausage.AppDataUsage.3
        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoaderReset(Loader<ArraySet<Preference>> loader) {
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public Loader<ArraySet<Preference>> onCreateLoader(int i, Bundle bundle) {
            return new AppPrefLoader(AppDataUsage.this.getPrefContext(), AppDataUsage.this.mPackages, AppDataUsage.this.getPackageManager());
        }

        @Override // androidx.loader.app.LoaderManager.LoaderCallbacks
        public void onLoadFinished(Loader<ArraySet<Preference>> loader, ArraySet<Preference> arraySet) {
            if (arraySet == null || AppDataUsage.this.mAppList == null) {
                return;
            }
            Iterator<Preference> it = arraySet.iterator();
            while (it.hasNext()) {
                AppDataUsage.this.mAppList.addPreference(it.next());
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppDataUsage";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 343;
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDataSaverChanged(boolean z) {
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        int intExtra;
        super.onCreate(bundle);
        this.mContext = getContext();
        this.mPackageManager = getPackageManager();
        Bundle arguments = getArguments();
        this.mAppItem = arguments != null ? (AppItem) arguments.getParcelable("app_item") : null;
        this.mTemplate = arguments != null ? (NetworkTemplate) arguments.getParcelable("network_template") : null;
        this.mCycles = arguments != null ? (ArrayList) arguments.getSerializable("network_cycles") : null;
        this.mSelectedCycle = arguments != null ? arguments.getLong("selected_cycle") : 0L;
        if (this.mTemplate == null) {
            this.mTemplate = DataUsageUtils.getDefaultTemplate(this.mContext, SubscriptionManager.getDefaultDataSubscriptionId());
        }
        boolean z = false;
        if (this.mAppItem == null) {
            if (arguments != null) {
                intExtra = arguments.getInt("uid", -1);
            } else {
                intExtra = getActivity().getIntent().getIntExtra("uid", -1);
            }
            if (intExtra == -1) {
                getActivity().finish();
            } else {
                addUid(intExtra);
                AppItem appItem = new AppItem(intExtra);
                this.mAppItem = appItem;
                appItem.addUid(intExtra);
            }
        } else {
            for (int i = 0; i < this.mAppItem.uids.size(); i++) {
                addUid(this.mAppItem.uids.keyAt(i));
            }
        }
        int i2 = this.mAppItem.key;
        if (i2 > 0 && UserHandle.isApp(i2)) {
            int sdkSandboxUid = Process.toSdkSandboxUid(this.mAppItem.key);
            if (!this.mAppItem.uids.get(sdkSandboxUid)) {
                this.mAppItem.addUid(sdkSandboxUid);
            }
        }
        this.mTotalUsage = findPreference("total_usage");
        this.mForegroundUsage = findPreference("foreground_usage");
        this.mBackgroundUsage = findPreference("background_usage");
        initCycle();
        UidDetailProvider uidDetailProvider = getUidDetailProvider();
        int i3 = this.mAppItem.key;
        if (i3 > 0) {
            if (!UserHandle.isApp(i3)) {
                UidDetail uidDetail = uidDetailProvider.getUidDetail(this.mAppItem.key, true);
                this.mIcon = uidDetail.icon;
                this.mLabel = uidDetail.label;
                removePreference("unrestricted_data_saver");
                removePreference("restrict_background");
            } else {
                if (this.mPackages.size() != 0) {
                    try {
                        ApplicationInfo applicationInfoAsUser = this.mPackageManager.getApplicationInfoAsUser(this.mPackages.valueAt(0), 0, UserHandle.getUserId(this.mAppItem.key));
                        this.mIcon = IconDrawableFactory.newInstance(getActivity()).getBadgedIcon(applicationInfoAsUser);
                        this.mLabel = applicationInfoAsUser.loadLabel(this.mPackageManager);
                        this.mPackageName = applicationInfoAsUser.packageName;
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
                RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) findPreference("restrict_background");
                this.mRestrictBackground = restrictedSwitchPreference;
                restrictedSwitchPreference.setOnPreferenceChangeListener(this);
                RestrictedSwitchPreference restrictedSwitchPreference2 = (RestrictedSwitchPreference) findPreference("unrestricted_data_saver");
                this.mUnrestrictedData = restrictedSwitchPreference2;
                restrictedSwitchPreference2.setOnPreferenceChangeListener(this);
            }
            this.mDataSaverBackend = new DataSaverBackend(this.mContext);
            this.mAppSettings = findPreference("app_settings");
            Intent intent = new Intent("android.intent.action.MANAGE_NETWORK_USAGE");
            this.mAppSettingsIntent = intent;
            intent.addCategory("android.intent.category.DEFAULT");
            PackageManager packageManager = getPackageManager();
            Iterator<String> it = this.mPackages.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                this.mAppSettingsIntent.setPackage(it.next());
                if (packageManager.resolveActivity(this.mAppSettingsIntent, 0) != null) {
                    z = true;
                    break;
                }
            }
            if (!z) {
                removePreference("app_settings");
                this.mAppSettings = null;
            }
            if (this.mPackages.size() > 1) {
                this.mAppList = (PreferenceCategory) findPreference("app_list");
                LoaderManager.getInstance(this).restartLoader(3, Bundle.EMPTY, this.mAppPrefCallbacks);
            } else {
                removePreference("app_list");
            }
        } else {
            FragmentActivity activity = getActivity();
            UidDetail uidDetail2 = uidDetailProvider.getUidDetail(this.mAppItem.key, true);
            this.mIcon = uidDetail2.icon;
            this.mLabel = uidDetail2.label;
            this.mPackageName = activity.getPackageName();
            removePreference("unrestricted_data_saver");
            removePreference("app_settings");
            removePreference("restrict_background");
            removePreference("app_list");
        }
        addEntityHeader();
    }

    @Override // com.android.settings.datausage.DataUsageBaseFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mIsLoading = true;
        getListView().setItemAnimator(null);
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            dataSaverBackend.addListener(this);
        }
        LoaderManager.getInstance(this).restartLoader(2, null, this.mUidDataCallbacks);
        updatePrefs();
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            dataSaverBackend.remListener(this);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        if (preference == this.mRestrictBackground) {
            this.mDataSaverBackend.setIsDenylisted(this.mAppItem.key, this.mPackageName, !((Boolean) obj).booleanValue());
            updatePrefs();
            return true;
        } else if (preference == this.mUnrestrictedData) {
            this.mDataSaverBackend.setIsAllowlisted(this.mAppItem.key, this.mPackageName, ((Boolean) obj).booleanValue());
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference == this.mAppSettings) {
            getActivity().startActivityAsUser(this.mAppSettingsIntent, new UserHandle(UserHandle.getUserId(this.mAppItem.key)));
            return true;
        }
        return super.onPreferenceTreeClick(preference);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.app_data_usage;
    }

    void updatePrefs() {
        updatePrefs(getAppRestrictBackground(), getUnrestrictData());
    }

    UidDetailProvider getUidDetailProvider() {
        return new UidDetailProvider(this.mContext);
    }

    private void initCycle() {
        this.mCycle = (SpinnerPreference) findPreference("cycle");
        CycleAdapter cycleAdapter = new CycleAdapter(this.mContext, this.mCycle, this.mCycleListener);
        this.mCycleAdapter = cycleAdapter;
        ArrayList<Long> arrayList = this.mCycles;
        if (arrayList != null) {
            cycleAdapter.setInitialCycleList(arrayList, this.mSelectedCycle);
            this.mCycle.setHasCycles(true);
        }
    }

    private void setBackPreferenceListAnimatorIfLoaded() {
        if (this.mIsLoading) {
            return;
        }
        RecyclerView listView = getListView();
        if (listView.getItemAnimator() == null) {
            listView.setItemAnimator(new DefaultItemAnimator());
        }
    }

    private void updatePrefs(boolean z, boolean z2) {
        setBackPreferenceListAnimatorIfLoaded();
        RestrictedLockUtils.EnforcedAdmin checkIfMeteredDataRestricted = RestrictedLockUtilsInternal.checkIfMeteredDataRestricted(this.mContext, this.mPackageName, UserHandle.getUserId(this.mAppItem.key));
        RestrictedSwitchPreference restrictedSwitchPreference = this.mRestrictBackground;
        if (restrictedSwitchPreference != null) {
            restrictedSwitchPreference.setChecked(!z);
            this.mRestrictBackground.setDisabledByAdmin(checkIfMeteredDataRestricted);
        }
        RestrictedSwitchPreference restrictedSwitchPreference2 = this.mUnrestrictedData;
        if (restrictedSwitchPreference2 != null) {
            if (z) {
                restrictedSwitchPreference2.setVisible(false);
                return;
            }
            restrictedSwitchPreference2.setVisible(true);
            this.mUnrestrictedData.setChecked(z2);
            this.mUnrestrictedData.setDisabledByAdmin(checkIfMeteredDataRestricted);
        }
    }

    private void addUid(int i) {
        if (Process.isSdkSandboxUid(i)) {
            i = Process.getAppUidForSdkSandboxUid(i);
        }
        String[] packagesForUid = this.mPackageManager.getPackagesForUid(i);
        if (packagesForUid != null) {
            for (String str : packagesForUid) {
                this.mPackages.add(str);
            }
        }
    }

    void bindData(int i) {
        long j;
        long j2;
        List<NetworkCycleDataForUid> list = this.mUsageData;
        if (list == null || i >= list.size()) {
            j = 0;
            this.mCycle.setHasCycles(false);
            j2 = 0;
        } else {
            this.mCycle.setHasCycles(true);
            NetworkCycleDataForUid networkCycleDataForUid = this.mUsageData.get(i);
            j = networkCycleDataForUid.getBackgroudUsage();
            j2 = networkCycleDataForUid.getForegroudUsage();
        }
        this.mTotalUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j + j2));
        this.mForegroundUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j2));
        this.mBackgroundUsage.setSummary(DataUsageUtils.formatDataUsage(this.mContext, j));
    }

    private boolean getAppRestrictBackground() {
        return (this.services.mPolicyManager.getUidPolicy(this.mAppItem.key) & 1) != 0;
    }

    private boolean getUnrestrictData() {
        DataSaverBackend dataSaverBackend = this.mDataSaverBackend;
        if (dataSaverBackend != null) {
            return dataSaverBackend.isAllowlisted(this.mAppItem.key);
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0042  */
    /* JADX WARN: Removed duplicated region for block: B:14:0x0044  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void addEntityHeader() {
        /*
            r8 = this;
            android.util.ArraySet<java.lang.String> r0 = r8.mPackages
            int r0 = r0.size()
            r1 = 0
            r2 = 0
            if (r0 == 0) goto L13
            android.util.ArraySet<java.lang.String> r0 = r8.mPackages
            java.lang.Object r0 = r0.valueAt(r2)
            java.lang.String r0 = (java.lang.String) r0
            goto L14
        L13:
            r0 = r1
        L14:
            if (r0 == 0) goto L3b
            android.content.pm.PackageManager r3 = r8.mPackageManager     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L25
            com.android.settingslib.AppItem r4 = r8.mAppItem     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L25
            int r4 = r4.key     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L25
            int r4 = android.os.UserHandle.getUserId(r4)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L25
            int r3 = r3.getPackageUidAsUser(r0, r4)     // Catch: android.content.pm.PackageManager.NameNotFoundException -> L25
            goto L3c
        L25:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Skipping UID because cannot find package "
            r3.append(r4)
            r3.append(r0)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "AppDataUsage"
            android.util.Log.w(r4, r3)
        L3b:
            r3 = r2
        L3c:
            com.android.settingslib.AppItem r4 = r8.mAppItem
            int r4 = r4.key
            if (r4 <= 0) goto L44
            r4 = 1
            goto L45
        L44:
            r4 = r2
        L45:
            androidx.fragment.app.FragmentActivity r5 = r8.getActivity()
            com.android.settings.widget.EntityHeaderController r1 = com.android.settings.widget.EntityHeaderController.newInstance(r5, r8, r1)
            androidx.recyclerview.widget.RecyclerView r6 = r8.getListView()
            com.android.settingslib.core.lifecycle.Lifecycle r7 = r8.getSettingsLifecycle()
            com.android.settings.widget.EntityHeaderController r1 = r1.setRecyclerView(r6, r7)
            com.android.settings.widget.EntityHeaderController r1 = r1.setUid(r3)
            com.android.settings.widget.EntityHeaderController r1 = r1.setHasAppInfoLink(r4)
            com.android.settings.widget.EntityHeaderController r1 = r1.setButtonActions(r2, r2)
            android.graphics.drawable.Drawable r2 = r8.mIcon
            com.android.settings.widget.EntityHeaderController r1 = r1.setIcon(r2)
            java.lang.CharSequence r2 = r8.mLabel
            com.android.settings.widget.EntityHeaderController r1 = r1.setLabel(r2)
            com.android.settings.widget.EntityHeaderController r0 = r1.setPackageName(r0)
            android.content.Context r1 = r8.getPrefContext()
            com.android.settingslib.widget.LayoutPreference r0 = r0.done(r5, r1)
            androidx.preference.PreferenceScreen r8 = r8.getPreferenceScreen()
            r8.addPreference(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.datausage.AppDataUsage.addEntityHeader():void");
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onAllowlistStatusChanged(int i, boolean z) {
        if (this.mAppItem.uids.get(i, false)) {
            updatePrefs(getAppRestrictBackground(), z);
        }
    }

    @Override // com.android.settings.datausage.DataSaverBackend.Listener
    public void onDenylistStatusChanged(int i, boolean z) {
        if (this.mAppItem.uids.get(i, false)) {
            updatePrefs(z, getUnrestrictData());
        }
    }
}
