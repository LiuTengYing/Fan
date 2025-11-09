package com.android.settings.applications.appinfo;

import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.om.OverlayManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.SettingsActivity;
import com.android.settings.applications.ApplicationFeatureProvider;
import com.android.settings.applications.appinfo.ButtonActionDialogFragment;
import com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.instrumentation.SettingsStatsLog;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.Utils;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.widget.ActionButtonsPreference;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
import java.util.ArrayList;
import java.util.HashSet;
/* loaded from: classes.dex */
public class AppButtonsPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnDestroy, ApplicationsState.Callbacks {
    public static final String APP_CHG = "chg";
    private static final String KEY_ACTION_BUTTONS = "action_buttons";
    public static final String KEY_REMOVE_TASK_WHEN_FINISHING = "remove_task_when_finishing";
    private static final boolean LOCAL_LOGV = false;
    private static final String TAG = "AppButtonsPrefCtl";
    private boolean mAccessedFromAutoRevoke;
    private final SettingsActivity mActivity;
    ApplicationsState.AppEntry mAppEntry;
    private Intent mAppLaunchIntent;
    private final ApplicationFeatureProvider mApplicationFeatureProvider;
    private RestrictedLockUtils.EnforcedAdmin mAppsControlDisallowedAdmin;
    private boolean mAppsControlDisallowedBySystem;
    ActionButtonsPreference mButtonsPref;
    private final BroadcastReceiver mCheckKillProcessesReceiver;
    boolean mDisableAfterUninstall;
    private final DevicePolicyManager mDpm;
    private boolean mFinishing;
    private final InstrumentedPreferenceFragment mFragment;
    final HashSet<String> mHomePackages;
    private boolean mListeningToPackageRemove;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final OverlayManager mOverlayManager;
    PackageInfo mPackageInfo;
    String mPackageName;
    private final BroadcastReceiver mPackageRemovedReceiver;
    private final PackageManager mPm;
    private final int mRequestRemoveDeviceAdmin;
    private final int mRequestUninstall;
    private PreferenceScreen mScreen;
    private ApplicationsState.Session mSession;
    private long mSessionId;
    ApplicationsState mState;
    private boolean mUpdatedSysApp;
    private final int mUserId;
    private final UserManager mUserManager;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_ACTION_BUTTONS;
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onAllSizesComputed() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLauncherInfoChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onLoadEntriesCompleted() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageIconChanged() {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AppButtonsPreferenceController(SettingsActivity settingsActivity, InstrumentedPreferenceFragment instrumentedPreferenceFragment, Lifecycle lifecycle, String str, ApplicationsState applicationsState, int i, int i2) {
        super(settingsActivity, KEY_ACTION_BUTTONS);
        this.mHomePackages = new HashSet<>();
        this.mDisableAfterUninstall = false;
        this.mUpdatedSysApp = false;
        this.mListeningToPackageRemove = false;
        this.mFinishing = false;
        this.mCheckKillProcessesReceiver = new BroadcastReceiver() { // from class: com.android.settings.applications.appinfo.AppButtonsPreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                boolean z = getResultCode() != 0;
                Log.d(AppButtonsPreferenceController.TAG, "Got broadcast response: Restart status for " + AppButtonsPreferenceController.this.mAppEntry.info.packageName + " " + z);
                AppButtonsPreferenceController.this.updateForceStopButtonInner(z);
            }
        };
        this.mPackageRemovedReceiver = new BroadcastReceiver() { // from class: com.android.settings.applications.appinfo.AppButtonsPreferenceController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
                if (AppButtonsPreferenceController.this.mFinishing || !AppButtonsPreferenceController.this.mAppEntry.info.packageName.equals(schemeSpecificPart)) {
                    return;
                }
                AppButtonsPreferenceController.this.mActivity.finishAndRemoveTask();
            }
        };
        if (!(instrumentedPreferenceFragment instanceof ButtonActionDialogFragment.AppButtonsDialogListener)) {
            throw new IllegalArgumentException("Fragment should implement AppButtonsDialogListener");
        }
        FeatureFactory factory = FeatureFactory.getFactory(settingsActivity);
        this.mMetricsFeatureProvider = factory.getMetricsFeatureProvider();
        this.mApplicationFeatureProvider = factory.getApplicationFeatureProvider(settingsActivity);
        this.mState = applicationsState;
        this.mDpm = (DevicePolicyManager) settingsActivity.getSystemService("device_policy");
        this.mUserManager = (UserManager) settingsActivity.getSystemService("user");
        PackageManager packageManager = settingsActivity.getPackageManager();
        this.mPm = packageManager;
        this.mOverlayManager = (OverlayManager) settingsActivity.getSystemService(OverlayManager.class);
        this.mPackageName = str;
        this.mActivity = settingsActivity;
        this.mFragment = instrumentedPreferenceFragment;
        int myUserId = UserHandle.myUserId();
        this.mUserId = myUserId;
        this.mRequestUninstall = i;
        this.mRequestRemoveDeviceAdmin = i2;
        this.mAppLaunchIntent = packageManager.getLaunchIntentForPackage(this.mPackageName);
        long longExtra = settingsActivity.getIntent().getLongExtra("android.intent.action.AUTO_REVOKE_PERMISSIONS", 0L);
        this.mSessionId = longExtra;
        this.mAccessedFromAutoRevoke = longExtra != 0;
        if (str != null) {
            this.mAppEntry = this.mState.getEntry(str, myUserId);
            this.mSession = this.mState.newSession(this, lifecycle);
            lifecycle.addObserver(this);
            return;
        }
        this.mFinishing = true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mFinishing || isInstantApp()) ? 4 : 0;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mScreen = preferenceScreen;
        if (isAvailable()) {
            initButtonPreference();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            this.mAppsControlDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mActivity, "no_control_apps", this.mUserId);
            this.mAppsControlDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mActivity, "no_control_apps", this.mUserId);
            if (refreshUi()) {
                return;
            }
            setIntentAndFinish(true, false);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        stopListeningToPackageRemove();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class UninstallAndDisableButtonListener implements View.OnClickListener {
        private UninstallAndDisableButtonListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (AppButtonsPreferenceController.this.mAccessedFromAutoRevoke) {
                Log.i(AppButtonsPreferenceController.TAG, "sessionId: " + AppButtonsPreferenceController.this.mSessionId + " uninstalling " + AppButtonsPreferenceController.this.mPackageName + " with uid " + AppButtonsPreferenceController.this.getUid() + ", reached from auto revoke");
                SettingsStatsLog.write(272, AppButtonsPreferenceController.this.mSessionId, AppButtonsPreferenceController.this.getUid(), AppButtonsPreferenceController.this.mPackageName, 5);
            }
            AppButtonsPreferenceController appButtonsPreferenceController = AppButtonsPreferenceController.this;
            String str = appButtonsPreferenceController.mAppEntry.info.packageName;
            if (appButtonsPreferenceController.mDpm.packageHasActiveAdmins(AppButtonsPreferenceController.this.mPackageInfo.packageName)) {
                AppButtonsPreferenceController.this.stopListeningToPackageRemove();
                Intent intent = new Intent(AppButtonsPreferenceController.this.mActivity, DeviceAdminAdd.class);
                intent.putExtra("android.app.extra.DEVICE_ADMIN_PACKAGE_NAME", str);
                AppButtonsPreferenceController.this.mMetricsFeatureProvider.action(AppButtonsPreferenceController.this.mActivity, 873, AppButtonsPreferenceController.this.getPackageNameForMetric());
                AppButtonsPreferenceController.this.mFragment.startActivityForResult(intent, AppButtonsPreferenceController.this.mRequestRemoveDeviceAdmin);
                return;
            }
            RestrictedLockUtils.EnforcedAdmin checkIfUninstallBlocked = RestrictedLockUtilsInternal.checkIfUninstallBlocked(AppButtonsPreferenceController.this.mActivity, str, AppButtonsPreferenceController.this.mUserId);
            boolean z = AppButtonsPreferenceController.this.mAppsControlDisallowedBySystem || RestrictedLockUtilsInternal.hasBaseUserRestriction(AppButtonsPreferenceController.this.mActivity, str, AppButtonsPreferenceController.this.mUserId);
            if (checkIfUninstallBlocked != null && !z) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(AppButtonsPreferenceController.this.mActivity, checkIfUninstallBlocked);
                return;
            }
            AppButtonsPreferenceController appButtonsPreferenceController2 = AppButtonsPreferenceController.this;
            ApplicationInfo applicationInfo = appButtonsPreferenceController2.mAppEntry.info;
            int i = applicationInfo.flags;
            if ((i & 1) == 0) {
                if ((8388608 & i) == 0) {
                    appButtonsPreferenceController2.uninstallPkg(str, true, false);
                } else {
                    appButtonsPreferenceController2.uninstallPkg(str, false, false);
                }
            } else if (applicationInfo.enabled && !appButtonsPreferenceController2.isDisabledUntilUsed()) {
                if (AppButtonsPreferenceController.this.mUpdatedSysApp && AppButtonsPreferenceController.this.isSingleUser()) {
                    AppButtonsPreferenceController.this.showDialogInner(1);
                } else {
                    AppButtonsPreferenceController.this.showDialogInner(0);
                }
            } else {
                MetricsFeatureProvider metricsFeatureProvider = AppButtonsPreferenceController.this.mMetricsFeatureProvider;
                SettingsActivity settingsActivity = AppButtonsPreferenceController.this.mActivity;
                AppButtonsPreferenceController appButtonsPreferenceController3 = AppButtonsPreferenceController.this;
                metricsFeatureProvider.action(settingsActivity, appButtonsPreferenceController3.mAppEntry.info.enabled ? 874 : 875, appButtonsPreferenceController3.getPackageNameForMetric());
                AppButtonsPreferenceController appButtonsPreferenceController4 = AppButtonsPreferenceController.this;
                AsyncTask.execute(new DisableChangerRunnable(appButtonsPreferenceController4.mPm, AppButtonsPreferenceController.this.mAppEntry.info.packageName, 0));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class ForceStopButtonListener implements View.OnClickListener {
        private ForceStopButtonListener() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            AppButtonsPreferenceController.this.mMetricsFeatureProvider.action(AppButtonsPreferenceController.this.mActivity, 1775, AppButtonsPreferenceController.this.getPackageNameForMetric());
            PackageManager packageManager = AppButtonsPreferenceController.this.mPm;
            AppButtonsPreferenceController appButtonsPreferenceController = AppButtonsPreferenceController.this;
            if (packageManager.isPackageStateProtected(appButtonsPreferenceController.mAppEntry.info.packageName, appButtonsPreferenceController.mUserId)) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(AppButtonsPreferenceController.this.mActivity, RestrictedLockUtilsInternal.getDeviceOwner(AppButtonsPreferenceController.this.mActivity));
            } else if (AppButtonsPreferenceController.this.mAppsControlDisallowedAdmin != null && !AppButtonsPreferenceController.this.mAppsControlDisallowedBySystem) {
                RestrictedLockUtils.sendShowAdminSupportDetailsIntent(AppButtonsPreferenceController.this.mActivity, AppButtonsPreferenceController.this.mAppsControlDisallowedAdmin);
            } else {
                AppButtonsPreferenceController.this.showDialogInner(2);
            }
        }
    }

    public void handleActivityResult(int i, int i2, Intent intent) {
        if (i == this.mRequestUninstall) {
            if (this.mDisableAfterUninstall) {
                this.mDisableAfterUninstall = false;
                AsyncTask.execute(new DisableChangerRunnable(this.mPm, this.mAppEntry.info.packageName, 3));
            }
            refreshAndFinishIfPossible(true);
        } else if (i == this.mRequestRemoveDeviceAdmin) {
            refreshAndFinishIfPossible(false);
        }
    }

    public void handleDialogClick(int i) {
        if (i == 0) {
            this.mMetricsFeatureProvider.action(this.mActivity, 874, new Pair[0]);
            AsyncTask.execute(new DisableChangerRunnable(this.mPm, this.mAppEntry.info.packageName, 3));
        } else if (i == 1) {
            this.mMetricsFeatureProvider.action(this.mActivity, 874, new Pair[0]);
            uninstallPkg(this.mAppEntry.info.packageName, false, true);
        } else if (i != 2) {
        } else {
            forceStopPackage(this.mAppEntry.info.packageName);
        }
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        if (isAvailable()) {
            refreshUi();
        }
    }

    void retrieveAppEntry() {
        ApplicationsState.AppEntry entry = this.mState.getEntry(this.mPackageName, this.mUserId);
        this.mAppEntry = entry;
        if (entry != null) {
            try {
                this.mPackageInfo = this.mPm.getPackageInfo(entry.info.packageName, 4198976);
                this.mPackageName = this.mAppEntry.info.packageName;
                return;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Exception when retrieving package:" + this.mAppEntry.info.packageName, e);
                this.mPackageInfo = null;
                return;
            }
        }
        this.mPackageInfo = null;
    }

    void updateOpenButton() {
        Intent launchIntentForPackage = this.mPm.getLaunchIntentForPackage(this.mPackageName);
        this.mAppLaunchIntent = launchIntentForPackage;
        this.mButtonsPref.setButton1Visible(launchIntentForPackage != null);
    }

    /* JADX WARN: Code restructure failed: missing block: B:57:0x0101, code lost:
        if (r7.mState.getEntry(r0.targetPackageName, android.os.UserHandle.getUserId(r7.mAppEntry.info.uid)) != null) goto L44;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    void updateUninstallButton() {
        /*
            Method dump skipped, instructions count: 267
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.applications.appinfo.AppButtonsPreferenceController.updateUninstallButton():void");
    }

    private void setIntentAndFinish(boolean z, boolean z2) {
        Intent intent = new Intent();
        intent.putExtra(APP_CHG, z);
        intent.putExtra(KEY_REMOVE_TASK_WHEN_FINISHING, z2);
        this.mActivity.finishPreferencePanel(-1, intent);
        this.mFinishing = true;
    }

    private void refreshAndFinishIfPossible(boolean z) {
        if (!refreshUi()) {
            setIntentAndFinish(true, z);
        } else {
            startListeningToPackageRemove();
        }
    }

    void updateForceStopButton() {
        if (this.mDpm.packageHasActiveAdmins(this.mPackageInfo.packageName)) {
            Log.w(TAG, "User can't force stop device admin");
            updateForceStopButtonInner(false);
        } else if ((this.mAppEntry.info.flags & 2097152) == 0) {
            Log.w(TAG, "App is not explicitly stopped");
            updateForceStopButtonInner(true);
        } else {
            Intent intent = new Intent("android.intent.action.QUERY_PACKAGE_RESTART", Uri.fromParts("package", this.mAppEntry.info.packageName, null));
            intent.putExtra("android.intent.extra.PACKAGES", new String[]{this.mAppEntry.info.packageName});
            intent.putExtra("android.intent.extra.UID", this.mAppEntry.info.uid);
            intent.putExtra("android.intent.extra.user_handle", UserHandle.getUserId(this.mAppEntry.info.uid));
            Log.d(TAG, "Sending broadcast to query restart status for " + this.mAppEntry.info.packageName);
            this.mActivity.sendOrderedBroadcastAsUser(intent, UserHandle.CURRENT, null, this.mCheckKillProcessesReceiver, null, 0, null, null);
        }
        if (UniMobileNetworkUtils.isAppDisableAllowed(this.mContext, this.mPackageInfo.packageName)) {
            return;
        }
        updateForceStopButtonInner(false);
    }

    void updateForceStopButtonInner(boolean z) {
        if (this.mAppsControlDisallowedBySystem) {
            this.mButtonsPref.setButton3Enabled(false);
        } else {
            this.mButtonsPref.setButton3Enabled(z);
        }
    }

    void uninstallPkg(String str, boolean z, boolean z2) {
        stopListeningToPackageRemove();
        Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + str));
        intent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", z);
        this.mMetricsFeatureProvider.action(this.mActivity, 872, new Pair[0]);
        this.mFragment.startActivityForResult(intent, this.mRequestUninstall);
        this.mDisableAfterUninstall = z2;
    }

    void forceStopPackage(String str) {
        MetricsFeatureProvider metricsFeatureProvider = this.mMetricsFeatureProvider;
        metricsFeatureProvider.action(metricsFeatureProvider.getAttribution(this.mActivity), 807, this.mFragment.getMetricsCategory(), str, 0);
        Log.d(TAG, "Stopping package " + str);
        ((ActivityManager) this.mActivity.getSystemService("activity")).forceStopPackage(str);
        int userId = UserHandle.getUserId(this.mAppEntry.info.uid);
        this.mState.invalidatePackage(str, userId);
        ApplicationsState.AppEntry entry = this.mState.getEntry(str, userId);
        if (entry != null) {
            this.mAppEntry = entry;
        }
        updateForceStopButton();
    }

    boolean handleDisableable() {
        boolean z = true;
        if (this.mHomePackages.contains(this.mAppEntry.info.packageName) || isSystemPackage(this.mActivity.getResources(), this.mPm, this.mPackageInfo)) {
            this.mButtonsPref.setButton2Text(R$string.disable_text).setButton2Icon(R$drawable.ic_settings_disable);
            z = false;
        } else if (this.mAppEntry.info.enabled && !isDisabledUntilUsed()) {
            this.mButtonsPref.setButton2Text(R$string.disable_text).setButton2Icon(R$drawable.ic_settings_disable);
            z = true ^ this.mApplicationFeatureProvider.getKeepEnabledPackages().contains(this.mAppEntry.info.packageName);
        } else {
            this.mButtonsPref.setButton2Text(R$string.enable_text).setButton2Icon(R$drawable.ic_settings_enable);
        }
        if (UniMobileNetworkUtils.isAppDisableAllowed(this.mContext, this.mAppEntry.info.packageName)) {
            return z;
        }
        return false;
    }

    boolean isSystemPackage(Resources resources, PackageManager packageManager, PackageInfo packageInfo) {
        return Utils.isSystemPackage(resources, packageManager, packageInfo);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isDisabledUntilUsed() {
        return this.mAppEntry.info.enabledSetting == 4;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showDialogInner(int i) {
        ButtonActionDialogFragment newInstance = ButtonActionDialogFragment.newInstance(i);
        newInstance.setTargetFragment(this.mFragment, 0);
        FragmentManager supportFragmentManager = this.mActivity.getSupportFragmentManager();
        newInstance.show(supportFragmentManager, "dialog " + i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isSingleUser() {
        return this.mUserManager.getUserCount() == 1;
    }

    private boolean signaturesMatch(String str, String str2) {
        if (str == null || str2 == null) {
            return false;
        }
        try {
            return this.mPm.checkSignatures(str, str2) >= 0;
        } catch (Exception unused) {
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean refreshUi() {
        boolean z = false;
        z = false;
        if (this.mPackageName == null) {
            return false;
        }
        retrieveAppEntry();
        if (this.mAppEntry != null && this.mPackageInfo != null) {
            ArrayList arrayList = new ArrayList();
            this.mPm.getHomeActivities(arrayList);
            this.mHomePackages.clear();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                ResolveInfo resolveInfo = (ResolveInfo) arrayList.get(i);
                String str = resolveInfo.activityInfo.packageName;
                this.mHomePackages.add(str);
                Bundle bundle = resolveInfo.activityInfo.metaData;
                if (bundle != null) {
                    String string = bundle.getString("android.app.home.alternate");
                    if (signaturesMatch(string, str)) {
                        this.mHomePackages.add(string);
                    }
                }
            }
            z = true;
            if (this.mButtonsPref == null) {
                initButtonPreference();
                this.mButtonsPref.setVisible(true);
            }
            updateOpenButton();
            updateUninstallButton();
            updateForceStopButton();
        }
        return z;
    }

    private void initButtonPreference() {
        this.mButtonsPref = ((ActionButtonsPreference) this.mScreen.findPreference(KEY_ACTION_BUTTONS)).setButton1Text(R$string.launch_instant_app).setButton1Icon(R$drawable.ic_settings_open).setButton1OnClickListener(new View.OnClickListener() { // from class: com.android.settings.applications.appinfo.AppButtonsPreferenceController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppButtonsPreferenceController.this.lambda$initButtonPreference$0(view);
            }
        }).setButton2Text(R$string.uninstall_text).setButton2Icon(R$drawable.ic_settings_delete).setButton2OnClickListener(new UninstallAndDisableButtonListener()).setButton3Text(R$string.force_stop).setButton3Icon(R$drawable.ic_settings_force_stop).setButton3OnClickListener(new ForceStopButtonListener()).setButton3Enabled(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initButtonPreference$0(View view) {
        launchApplication();
    }

    private void startListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            return;
        }
        this.mListeningToPackageRemove = true;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        this.mActivity.registerReceiver(this.mPackageRemovedReceiver, intentFilter);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = false;
            this.mActivity.unregisterReceiver(this.mPackageRemovedReceiver);
        }
    }

    private void launchApplication() {
        if (this.mAppLaunchIntent != null) {
            if (this.mAccessedFromAutoRevoke) {
                Log.i(TAG, "sessionId: " + this.mSessionId + " uninstalling " + this.mPackageName + " with uid " + getUid() + ", reached from auto revoke");
                SettingsStatsLog.write(272, this.mSessionId, getUid(), this.mPackageName, 6);
            }
            this.mContext.startActivityAsUser(this.mAppLaunchIntent, new UserHandle(this.mUserId));
            this.mMetricsFeatureProvider.action(this.mActivity, 1773, this.mPackageName);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getUid() {
        if (this.mPackageInfo == null) {
            retrieveAppEntry();
        }
        PackageInfo packageInfo = this.mPackageInfo;
        if (packageInfo != null) {
            return packageInfo.applicationInfo.uid;
        }
        return -1;
    }

    private boolean isInstantApp() {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        return appEntry != null && AppUtils.isInstant(appEntry.info);
    }

    private boolean isSystemModule() {
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        return appEntry != null && (AppUtils.isSystemModule(this.mContext, appEntry.info.packageName) || AppUtils.isMainlineModule(this.mPm, this.mAppEntry.info.packageName));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String getPackageNameForMetric() {
        ApplicationInfo applicationInfo;
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        String str = (appEntry == null || (applicationInfo = appEntry.info) == null) ? null : applicationInfo.packageName;
        return str != null ? str : "";
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DisableChangerRunnable implements Runnable {
        final String mPackageName;
        final PackageManager mPm;
        final int mState;

        public DisableChangerRunnable(PackageManager packageManager, String str, int i) {
            this.mPm = packageManager;
            this.mPackageName = str;
            this.mState = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mPm.setApplicationEnabledSetting(this.mPackageName, this.mState, 0);
        }
    }
}
