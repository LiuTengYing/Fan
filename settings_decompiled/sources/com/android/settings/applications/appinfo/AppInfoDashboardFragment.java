package com.android.settings.applications.appinfo;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricManager;
import android.hardware.biometrics.BiometricPrompt;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.SettingsActivity;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.applications.appinfo.ButtonActionDialogFragment;
import com.android.settings.applications.specialaccess.interactacrossprofiles.InteractAcrossProfilesDetailsPreferenceController;
import com.android.settings.applications.specialaccess.pictureinpicture.PictureInPictureDetailPreferenceController;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.applications.AppUtils;
import com.android.settingslib.applications.ApplicationsState;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public class AppInfoDashboardFragment extends DashboardFragment implements ApplicationsState.Callbacks, ButtonActionDialogFragment.AppButtonsDialogListener {
    static final int REQUEST_UNINSTALL = 0;
    static final int UNINSTALL_ALL_USERS_MENU = 1;
    static final int UNINSTALL_UPDATES = 2;
    private AppButtonsPreferenceController mAppButtonsPreferenceController;
    private ApplicationsState.AppEntry mAppEntry;
    private RestrictedLockUtils.EnforcedAdmin mAppsControlDisallowedAdmin;
    private boolean mAppsControlDisallowedBySystem;
    private DevicePolicyManager mDpm;
    boolean mFinishing;
    private boolean mInitialized;
    private InstantAppButtonsPreferenceController mInstantAppButtonPreferenceController;
    private boolean mListeningToPackageRemove;
    private PackageInfo mPackageInfo;
    private String mPackageName;
    private PackageManager mPm;
    private ApplicationsState.Session mSession;
    private boolean mShowUninstalled;
    private ApplicationsState mState;
    private int mUid;
    private int mUserId;
    private UserManager mUserManager;
    private boolean mUpdatedSysApp = false;
    private List<Callback> mCallbacks = new ArrayList();
    final BroadcastReceiver mPackageRemovedReceiver = new BroadcastReceiver() { // from class: com.android.settings.applications.appinfo.AppInfoDashboardFragment.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (AppInfoDashboardFragment.this.mFinishing) {
                return;
            }
            String schemeSpecificPart = intent.getData().getSchemeSpecificPart();
            if (AppInfoDashboardFragment.this.mAppEntry == null || AppInfoDashboardFragment.this.mAppEntry.info == null || TextUtils.equals(AppInfoDashboardFragment.this.mAppEntry.info.packageName, schemeSpecificPart)) {
                AppInfoDashboardFragment.this.onPackageRemoved();
            } else if (AppInfoDashboardFragment.this.mAppEntry.info.isResourceOverlay() && TextUtils.equals(AppInfoDashboardFragment.this.mPackageInfo.overlayTarget, schemeSpecificPart)) {
                AppInfoDashboardFragment.this.refreshUi();
            }
        }
    };

    /* loaded from: classes.dex */
    public interface Callback {
        void refreshUi();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "AppInfoDashboard";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 20;
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
    public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> arrayList) {
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onRunningStateChanged(boolean z) {
    }

    @Override // com.android.settings.SettingsPreferenceFragment
    protected boolean shouldSkipForInitialSUW() {
        return true;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        String packageName = getPackageName();
        TimeSpentInAppPreferenceController timeSpentInAppPreferenceController = (TimeSpentInAppPreferenceController) use(TimeSpentInAppPreferenceController.class);
        timeSpentInAppPreferenceController.setPackageName(packageName);
        timeSpentInAppPreferenceController.setParentFragment(this);
        timeSpentInAppPreferenceController.initLifeCycleOwner(this);
        ((AppDataUsagePreferenceController) use(AppDataUsagePreferenceController.class)).setParentFragment(this);
        AppInstallerInfoPreferenceController appInstallerInfoPreferenceController = (AppInstallerInfoPreferenceController) use(AppInstallerInfoPreferenceController.class);
        appInstallerInfoPreferenceController.setPackageName(packageName);
        appInstallerInfoPreferenceController.setParentFragment(this);
        ((AppInstallerPreferenceCategoryController) use(AppInstallerPreferenceCategoryController.class)).setChildren(Arrays.asList(appInstallerInfoPreferenceController));
        ((AppNotificationPreferenceController) use(AppNotificationPreferenceController.class)).setParentFragment(this);
        ((AppOpenByDefaultPreferenceController) use(AppOpenByDefaultPreferenceController.class)).setPackageName(packageName).setParentFragment(this);
        ((AppPermissionPreferenceController) use(AppPermissionPreferenceController.class)).setParentFragment(this);
        ((AppPermissionPreferenceController) use(AppPermissionPreferenceController.class)).setPackageName(packageName);
        ((AppSettingPreferenceController) use(AppSettingPreferenceController.class)).setPackageName(packageName).setParentFragment(this);
        ((AppAllServicesPreferenceController) use(AppAllServicesPreferenceController.class)).setParentFragment(this);
        ((AppAllServicesPreferenceController) use(AppAllServicesPreferenceController.class)).setPackageName(packageName);
        ((AppStoragePreferenceController) use(AppStoragePreferenceController.class)).setParentFragment(this);
        ((AppVersionPreferenceController) use(AppVersionPreferenceController.class)).setParentFragment(this);
        ((InstantAppDomainsPreferenceController) use(InstantAppDomainsPreferenceController.class)).setParentFragment(this);
        HibernationSwitchPreferenceController hibernationSwitchPreferenceController = (HibernationSwitchPreferenceController) use(HibernationSwitchPreferenceController.class);
        hibernationSwitchPreferenceController.setParentFragment(this);
        hibernationSwitchPreferenceController.setPackage(packageName);
        ((AppHibernationPreferenceCategoryController) use(AppHibernationPreferenceCategoryController.class)).setChildren(Arrays.asList(hibernationSwitchPreferenceController));
        WriteSystemSettingsPreferenceController writeSystemSettingsPreferenceController = (WriteSystemSettingsPreferenceController) use(WriteSystemSettingsPreferenceController.class);
        writeSystemSettingsPreferenceController.setParentFragment(this);
        DrawOverlayDetailPreferenceController drawOverlayDetailPreferenceController = (DrawOverlayDetailPreferenceController) use(DrawOverlayDetailPreferenceController.class);
        drawOverlayDetailPreferenceController.setParentFragment(this);
        PictureInPictureDetailPreferenceController pictureInPictureDetailPreferenceController = (PictureInPictureDetailPreferenceController) use(PictureInPictureDetailPreferenceController.class);
        pictureInPictureDetailPreferenceController.setPackageName(packageName);
        pictureInPictureDetailPreferenceController.setParentFragment(this);
        ExternalSourceDetailPreferenceController externalSourceDetailPreferenceController = (ExternalSourceDetailPreferenceController) use(ExternalSourceDetailPreferenceController.class);
        externalSourceDetailPreferenceController.setPackageName(packageName);
        externalSourceDetailPreferenceController.setParentFragment(this);
        InteractAcrossProfilesDetailsPreferenceController interactAcrossProfilesDetailsPreferenceController = (InteractAcrossProfilesDetailsPreferenceController) use(InteractAcrossProfilesDetailsPreferenceController.class);
        interactAcrossProfilesDetailsPreferenceController.setPackageName(packageName);
        interactAcrossProfilesDetailsPreferenceController.setParentFragment(this);
        AlarmsAndRemindersDetailPreferenceController alarmsAndRemindersDetailPreferenceController = (AlarmsAndRemindersDetailPreferenceController) use(AlarmsAndRemindersDetailPreferenceController.class);
        alarmsAndRemindersDetailPreferenceController.setPackageName(packageName);
        alarmsAndRemindersDetailPreferenceController.setParentFragment(this);
        ((AdvancedAppInfoPreferenceCategoryController) use(AdvancedAppInfoPreferenceCategoryController.class)).setChildren(Arrays.asList(writeSystemSettingsPreferenceController, drawOverlayDetailPreferenceController, pictureInPictureDetailPreferenceController, externalSourceDetailPreferenceController, interactAcrossProfilesDetailsPreferenceController, alarmsAndRemindersDetailPreferenceController));
        ((AppLocalePreferenceController) use(AppLocalePreferenceController.class)).setParentFragment(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mFinishing = false;
        FragmentActivity activity = getActivity();
        this.mDpm = (DevicePolicyManager) activity.getSystemService("device_policy");
        this.mUserManager = (UserManager) activity.getSystemService("user");
        this.mPm = activity.getPackageManager();
        if (ensurePackageInfoAvailable(activity) && ensureDisplayableModule(activity)) {
            startListeningToPackageRemove();
            setHasOptionsMenu(true);
            replaceEnterpriseStringTitle("interact_across_profiles", "Settings.CONNECTED_WORK_AND_PERSONAL_APPS_TITLE", R$string.interact_across_profiles_title);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        if (ensurePackageInfoAvailable(getActivity())) {
            super.onCreatePreferences(bundle, str);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        stopListeningToPackageRemove();
        super.onDestroy();
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        preferenceClick(preference.getKey());
        return super.onPreferenceTreeClick(preference);
    }

    private void preferenceClick(String str) {
        if ("app_uninstall_update".equals(str)) {
            boolean z = getContext().getResources().getBoolean(R$bool.config_disable_uninstall_update);
            if (!this.mUserManager.isAdminUser() || !this.mUpdatedSysApp || this.mAppsControlDisallowedBySystem || z) {
                return;
            }
            uninstallPkg(this.mAppEntry.info.packageName, false, false);
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        FragmentActivity activity = getActivity();
        this.mAppsControlDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(activity, "no_control_apps", this.mUserId);
        this.mAppsControlDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(activity, "no_control_apps", this.mUserId);
        if (!refreshUi()) {
            setIntentAndFinish(true, true);
        }
        getActivity().invalidateOptionsMenu();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.app_info_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        retrieveAppEntry();
        if (this.mPackageInfo == null) {
            return null;
        }
        String packageName = getPackageName();
        ArrayList<AbstractPreferenceController> arrayList = new ArrayList();
        Lifecycle settingsLifecycle = getSettingsLifecycle();
        arrayList.add(new AppHeaderViewPreferenceController(context, this, packageName, settingsLifecycle));
        for (AbstractPreferenceController abstractPreferenceController : arrayList) {
            this.mCallbacks.add((Callback) abstractPreferenceController);
        }
        InstantAppButtonsPreferenceController instantAppButtonsPreferenceController = new InstantAppButtonsPreferenceController(context, this, packageName, settingsLifecycle);
        this.mInstantAppButtonPreferenceController = instantAppButtonsPreferenceController;
        arrayList.add(instantAppButtonsPreferenceController);
        AppButtonsPreferenceController appButtonsPreferenceController = new AppButtonsPreferenceController((SettingsActivity) getActivity(), this, settingsLifecycle, packageName, this.mState, 0, 5);
        this.mAppButtonsPreferenceController = appButtonsPreferenceController;
        arrayList.add(appButtonsPreferenceController);
        arrayList.add(new AppBatteryPreferenceController(context, this, packageName, getUid(), settingsLifecycle));
        arrayList.add(new AppMemoryPreferenceController(context, this, settingsLifecycle));
        arrayList.add(new DefaultHomeShortcutPreferenceController(context, packageName));
        arrayList.add(new DefaultBrowserShortcutPreferenceController(context, packageName));
        arrayList.add(new DefaultPhoneShortcutPreferenceController(context, packageName));
        arrayList.add(new DefaultEmergencyShortcutPreferenceController(context, packageName));
        arrayList.add(new DefaultSmsShortcutPreferenceController(context, packageName));
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addToCallbackList(Callback callback) {
        if (callback != null) {
            this.mCallbacks.add(callback);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ApplicationsState.AppEntry getAppEntry() {
        return this.mAppEntry;
    }

    public PackageInfo getPackageInfo() {
        return this.mPackageInfo;
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageSizeChanged(String str) {
        if (!TextUtils.equals(str, this.mPackageName)) {
            Log.d("AppInfoDashboard", "Package change irrelevant, skipping");
        } else {
            refreshUi();
        }
    }

    boolean ensurePackageInfoAvailable(Activity activity) {
        if (this.mPackageInfo == null) {
            this.mFinishing = true;
            Log.w("AppInfoDashboard", "Package info not available. Is this package already uninstalled?");
            activity.finishAndRemoveTask();
            return false;
        }
        return true;
    }

    boolean ensureDisplayableModule(Activity activity) {
        if (AppUtils.isHiddenSystemModule(activity.getApplicationContext(), this.mPackageName)) {
            this.mFinishing = true;
            Log.w("AppInfoDashboard", "Package is hidden module, exiting: " + this.mPackageName);
            activity.finishAndRemoveTask();
            return false;
        }
        return true;
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menu.add(0, 2, 0, R$string.app_factory_reset).setShowAsAction(0);
        menu.add(0, 1, 1, R$string.uninstall_all_users_text).setShowAsAction(0);
        menu.add(0, 4, 0, R$string.app_restricted_settings_lockscreen_title).setShowAsAction(0);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        if (this.mFinishing) {
            return;
        }
        super.onPrepareOptionsMenu(menu);
        boolean z = true;
        MenuItem findItem = menu.findItem(1);
        findItem.setVisible(shouldShowUninstallForAll(this.mAppEntry) && !this.mAppsControlDisallowedBySystem);
        if (findItem.isVisible()) {
            RestrictedLockUtilsInternal.setMenuItemAsDisabledByAdmin(getActivity(), findItem, this.mAppsControlDisallowedAdmin);
        }
        menu.findItem(4).setVisible(shouldShowAccessRestrictedSettings());
        this.mUpdatedSysApp = (this.mAppEntry.info.flags & 128) != 0;
        MenuItem findItem2 = menu.findItem(2);
        boolean z2 = getContext().getResources().getBoolean(R$bool.config_disable_uninstall_update);
        if (!this.mUserManager.isAdminUser() || !this.mUpdatedSysApp || this.mAppsControlDisallowedBySystem || z2) {
            z = false;
        }
        findItem2.setVisible(z);
        if (findItem2.isVisible()) {
            RestrictedLockUtilsInternal.setMenuItemAsDisabledByAdmin(getActivity(), findItem2, this.mAppsControlDisallowedAdmin);
        }
    }

    private static void showLockScreen(Context context, final Runnable runnable) {
        if (((KeyguardManager) context.getSystemService(KeyguardManager.class)).isKeyguardSecure()) {
            BiometricPrompt.AuthenticationCallback authenticationCallback = new BiometricPrompt.AuthenticationCallback() { // from class: com.android.settings.applications.appinfo.AppInfoDashboardFragment.1
                @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationError(int i, CharSequence charSequence) {
                }

                @Override // android.hardware.biometrics.BiometricPrompt.AuthenticationCallback
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult authenticationResult) {
                    runnable.run();
                }
            };
            BiometricPrompt.Builder useDefaultTitle = new BiometricPrompt.Builder(context).setUseDefaultTitle();
            BiometricManager biometricManager = (BiometricManager) context.getSystemService(BiometricManager.class);
            if (biometricManager.canAuthenticate(33023) == 0) {
                useDefaultTitle.setAllowedAuthenticators(33023);
                useDefaultTitle.setSubtitle(biometricManager.getStrings(33023).getPromptMessage());
            }
            BiometricPrompt build = useDefaultTitle.build();
            final Handler handler = new Handler(Looper.getMainLooper());
            build.authenticate(new CancellationSignal(), new Executor() { // from class: com.android.settings.applications.appinfo.AppInfoDashboardFragment$$ExternalSyntheticLambda1
                @Override // java.util.concurrent.Executor
                public final void execute(Runnable runnable2) {
                    handler.post(runnable2);
                }
            }, authenticationCallback);
            return;
        }
        runnable.run();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 1) {
            uninstallPkg(this.mAppEntry.info.packageName, true, false);
            return true;
        } else if (itemId == 2) {
            uninstallPkg(this.mAppEntry.info.packageName, false, false);
            return true;
        } else if (itemId == 4) {
            showLockScreen(getContext(), new Runnable() { // from class: com.android.settings.applications.appinfo.AppInfoDashboardFragment$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AppInfoDashboardFragment.this.lambda$onOptionsItemSelected$1();
                }
            });
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onOptionsItemSelected$1() {
        ((AppOpsManager) getContext().getSystemService(AppOpsManager.class)).setMode(119, getUid(), getPackageName(), 0);
        getActivity().invalidateOptionsMenu();
        Toast.makeText(getContext(), getContext().getString(R$string.toast_allows_restricted_settings_successfully, this.mAppEntry.label), 1).show();
    }

    @Override // androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 0) {
            getActivity().invalidateOptionsMenu();
        }
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

    private boolean shouldShowAccessRestrictedSettings() {
        try {
            return ((AppOpsManager) getSystemService(AppOpsManager.class)).noteOpNoThrow(119, getUid(), getPackageName()) == 1;
        } catch (Exception unused) {
            return false;
        }
    }

    boolean shouldShowUninstallForAll(ApplicationsState.AppEntry appEntry) {
        PackageInfo packageInfo;
        if (this.mUpdatedSysApp || appEntry == null || (packageInfo = this.mPackageInfo) == null || this.mDpm.packageHasActiveAdmins(packageInfo.packageName) || UserHandle.myUserId() != 0 || this.mUserManager.getUsers().size() < 2) {
            return false;
        }
        return (getNumberOfUserWithPackageInstalled(this.mPackageName) >= 2 || (appEntry.info.flags & 8388608) == 0) && !AppUtils.isInstant(appEntry.info);
    }

    boolean refreshUi() {
        retrieveAppEntry();
        ApplicationsState.AppEntry appEntry = this.mAppEntry;
        if (appEntry == null || this.mPackageInfo == null) {
            return false;
        }
        this.mState.ensureIcon(appEntry);
        for (Callback callback : this.mCallbacks) {
            callback.refreshUi();
        }
        if (this.mAppButtonsPreferenceController.isAvailable()) {
            this.mAppButtonsPreferenceController.refreshUi();
        }
        if (!this.mInitialized) {
            this.mInitialized = true;
            this.mShowUninstalled = (this.mAppEntry.info.flags & 8388608) == 0;
        } else {
            try {
                ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo(this.mAppEntry.info.packageName, 4194816);
                if (!this.mShowUninstalled) {
                    return (applicationInfo.flags & 8388608) != 0;
                }
            } catch (PackageManager.NameNotFoundException unused) {
                return false;
            }
        }
        return true;
    }

    private void uninstallPkg(String str, boolean z, boolean z2) {
        stopListeningToPackageRemove();
        Intent intent = new Intent("android.intent.action.UNINSTALL_PACKAGE", Uri.parse("package:" + str));
        intent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", z);
        this.mMetricsFeatureProvider.action(getContext(), 872, new Pair[0]);
        startActivityForResult(intent, 0);
    }

    public static void startAppInfoFragment(Class<?> cls, int i, Bundle bundle, SettingsPreferenceFragment settingsPreferenceFragment, ApplicationsState.AppEntry appEntry) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("package", appEntry.info.packageName);
        bundle.putInt("uid", appEntry.info.uid);
        new SubSettingLauncher(settingsPreferenceFragment.getContext()).setDestination(cls.getName()).setArguments(bundle).setTitleRes(i).setResultListener(settingsPreferenceFragment, 1).setSourceMetricsCategory(settingsPreferenceFragment.getMetricsCategory()).launch();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPackageRemoved() {
        getActivity().finishActivity(1);
        getActivity().finishAndRemoveTask();
    }

    int getNumberOfUserWithPackageInstalled(String str) {
        int i = 0;
        for (UserInfo userInfo : this.mUserManager.getAliveUsers()) {
            try {
                if ((this.mPm.getApplicationInfoAsUser(str, 128, userInfo.id).flags & 8388608) != 0) {
                    i++;
                }
            } catch (PackageManager.NameNotFoundException unused) {
                Log.e("AppInfoDashboard", "Package: " + str + " not found for user: " + userInfo.id);
            }
        }
        return i;
    }

    private String getPackageName() {
        String str = this.mPackageName;
        if (str != null) {
            return str;
        }
        Bundle arguments = getArguments();
        String string = arguments != null ? arguments.getString("package") : null;
        this.mPackageName = string;
        if (string == null) {
            Intent intent = arguments == null ? getActivity().getIntent() : (Intent) arguments.getParcelable("intent");
            if (intent != null) {
                this.mPackageName = intent.getData().getSchemeSpecificPart();
            }
        }
        return this.mPackageName;
    }

    private int getUid() {
        int i = this.mUid;
        if (i > 0) {
            return i;
        }
        Bundle arguments = getArguments();
        int i2 = -1;
        int i3 = arguments != null ? arguments.getInt("uid") : -1;
        this.mUid = i3;
        if (i3 <= 0) {
            Intent intent = arguments == null ? getActivity().getIntent() : (Intent) arguments.getParcelable("intent");
            if (intent != null && intent.getExtras() != null) {
                i2 = intent.getIntExtra("uId", -1);
                this.mUid = i2;
            }
            this.mUid = i2;
        }
        return this.mUid;
    }

    void retrieveAppEntry() {
        FragmentActivity activity = getActivity();
        if (activity == null || this.mFinishing) {
            return;
        }
        if (this.mState == null) {
            ApplicationsState applicationsState = ApplicationsState.getInstance(activity.getApplication());
            this.mState = applicationsState;
            this.mSession = applicationsState.newSession(this, getSettingsLifecycle());
        }
        this.mUserId = UserHandle.myUserId();
        ApplicationsState.AppEntry entry = this.mState.getEntry(getPackageName(), UserHandle.myUserId());
        this.mAppEntry = entry;
        if (entry != null) {
            try {
                this.mPackageInfo = activity.getPackageManager().getPackageInfo(this.mAppEntry.info.packageName, 4198976);
                return;
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("AppInfoDashboard", "Exception when retrieving package:" + this.mAppEntry.info.packageName, e);
                return;
            }
        }
        Log.w("AppInfoDashboard", "Missing AppEntry; maybe reinstalling?");
        this.mPackageInfo = null;
    }

    private void setIntentAndFinish(boolean z, boolean z2) {
        Intent intent = new Intent();
        intent.putExtra(AppButtonsPreferenceController.APP_CHG, z2);
        ((SettingsActivity) getActivity()).finishPreferencePanel(-1, intent);
        this.mFinishing = true;
    }

    @Override // com.android.settingslib.applications.ApplicationsState.Callbacks
    public void onPackageListChanged() {
        if (refreshUi()) {
            return;
        }
        setIntentAndFinish(true, true);
    }

    void startListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            return;
        }
        this.mListeningToPackageRemove = true;
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addDataScheme("package");
        getContext().registerReceiver(this.mPackageRemovedReceiver, intentFilter);
    }

    private void stopListeningToPackageRemove() {
        if (this.mListeningToPackageRemove) {
            this.mListeningToPackageRemove = false;
            getContext().unregisterReceiver(this.mPackageRemovedReceiver);
        }
    }
}
