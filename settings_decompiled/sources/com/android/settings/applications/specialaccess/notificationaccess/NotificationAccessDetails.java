package com.android.settings.applications.specialaccess.notificationaccess;

import android.app.NotificationManager;
import android.companion.ICompanionDeviceManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Slog;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.bluetooth.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.notification.NotificationBackend;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class NotificationAccessDetails extends DashboardFragment {
    protected RestrictedLockUtils.EnforcedAdmin mAppsControlDisallowedAdmin;
    protected boolean mAppsControlDisallowedBySystem;
    private ComponentName mComponentName;
    private boolean mIsNls;
    private NotificationBackend mNm = new NotificationBackend();
    protected PackageInfo mPackageInfo;
    protected String mPackageName;
    private PackageManager mPm;
    protected ServiceInfo mServiceInfo;
    private CharSequence mServiceName;
    protected int mUserId;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NotifAccessDetails";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1804;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        String stringExtra;
        super.onAttach(context);
        Intent intent = getIntent();
        if (this.mComponentName == null && intent != null && (stringExtra = intent.getStringExtra("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME")) != null) {
            ComponentName unflattenFromString = ComponentName.unflattenFromString(stringExtra);
            this.mComponentName = unflattenFromString;
            if (unflattenFromString != null) {
                getArguments().putString("package", this.mComponentName.getPackageName());
            }
        }
        this.mPm = getPackageManager();
        retrieveAppEntry();
        loadNotificationListenerService();
        final NotificationBackend notificationBackend = new NotificationBackend();
        final int i = 31;
        try {
            i = this.mPm.getTargetSdkVersion(this.mComponentName.getPackageName());
        } catch (PackageManager.NameNotFoundException unused) {
        }
        ((ApprovalPreferenceController) use(ApprovalPreferenceController.class)).setPkgInfo(this.mPackageInfo).setCn(this.mComponentName).setNm((NotificationManager) context.getSystemService(NotificationManager.class)).setPm(this.mPm).setParent(this);
        ((HeaderPreferenceController) use(HeaderPreferenceController.class)).setFragment(this).setPackageInfo(this.mPackageInfo).setPm(context.getPackageManager()).setServiceName(this.mServiceName).setBluetoothManager(Utils.getLocalBtManager(context)).setCdm(ICompanionDeviceManager.Stub.asInterface(ServiceManager.getService("companiondevice"))).setCn(this.mComponentName).setUserId(this.mUserId);
        ((PreUpgradePreferenceController) use(PreUpgradePreferenceController.class)).setNm(notificationBackend).setCn(this.mComponentName).setUserId(this.mUserId).setTargetSdk(i);
        ((BridgedAppsLinkPreferenceController) use(BridgedAppsLinkPreferenceController.class)).setNm(notificationBackend).setCn(this.mComponentName).setUserId(this.mUserId).setTargetSdk(i);
        getPreferenceControllers().forEach(new Consumer() { // from class: com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationAccessDetails.this.lambda$onAttach$1(notificationBackend, i, (List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$1(final NotificationBackend notificationBackend, final int i, List list) {
        list.forEach(new Consumer() { // from class: com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationAccessDetails.this.lambda$onAttach$0(notificationBackend, i, (AbstractPreferenceController) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAttach$0(NotificationBackend notificationBackend, int i, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof TypeFilterPreferenceController) {
            ((TypeFilterPreferenceController) abstractPreferenceController).setNm(notificationBackend).setCn(this.mComponentName).setServiceInfo(this.mServiceInfo).setUserId(this.mUserId).setTargetSdk(i);
        }
    }

    protected boolean refreshUi() {
        if (this.mComponentName == null) {
            Slog.d("NotifAccessDetails", "No component name provided");
            return false;
        } else if (!this.mIsNls) {
            Slog.d("NotifAccessDetails", "Provided component name is not an NLS");
            return false;
        } else if (UserManager.get(getContext()).isManagedProfile()) {
            Slog.d("NotifAccessDetails", "NLSes aren't allowed in work profiles");
            return false;
        } else {
            return true;
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppsControlDisallowedAdmin = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(getActivity(), "no_control_apps", this.mUserId);
        this.mAppsControlDisallowedBySystem = RestrictedLockUtilsInternal.hasBaseUserRestriction(getActivity(), "no_control_apps", this.mUserId);
        if (!refreshUi()) {
            finish();
        }
        Preference findPreference = getPreferenceScreen().findPreference(((BridgedAppsLinkPreferenceController) use(BridgedAppsLinkPreferenceController.class)).getPreferenceKey());
        if (findPreference != null) {
            findPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails$$ExternalSyntheticLambda1
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$onResume$2;
                    lambda$onResume$2 = NotificationAccessDetails.this.lambda$onResume$2(preference);
                    return lambda$onResume$2;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$onResume$2(Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", this.mPackageName);
        bundle.putString("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME", this.mComponentName.flattenToString());
        new SubSettingLauncher(getContext()).setDestination(BridgedAppsSettings.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setTitleRes(R$string.notif_listener_excluded_app_screen_title).setArguments(bundle).setUserHandle(UserHandle.of(this.mUserId)).launch();
        return true;
    }

    protected void retrieveAppEntry() {
        Bundle arguments = getArguments();
        this.mPackageName = arguments != null ? arguments.getString("package") : null;
        Intent intent = arguments == null ? getIntent() : (Intent) arguments.getParcelable("intent");
        if (this.mPackageName == null && intent != null && intent.getData() != null) {
            this.mPackageName = intent.getData().getSchemeSpecificPart();
        }
        if (intent != null && intent.hasExtra("android.intent.extra.user_handle")) {
            this.mUserId = ((UserHandle) intent.getParcelableExtra("android.intent.extra.user_handle")).getIdentifier();
        } else {
            this.mUserId = UserHandle.myUserId();
        }
        try {
            this.mPackageInfo = this.mPm.getPackageInfoAsUser(this.mPackageName, 134222336, this.mUserId);
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }

    public void disable(ComponentName componentName) {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        ApprovalPreferenceController approvalPreferenceController = (ApprovalPreferenceController) use(ApprovalPreferenceController.class);
        approvalPreferenceController.disable(componentName);
        approvalPreferenceController.updateState(preferenceScreen.findPreference(approvalPreferenceController.getPreferenceKey()));
        getPreferenceControllers().forEach(new Consumer() { // from class: com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails$$ExternalSyntheticLambda3
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationAccessDetails.lambda$disable$4(PreferenceScreen.this, (List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$disable$4(final PreferenceScreen preferenceScreen, List list) {
        list.forEach(new Consumer() { // from class: com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails$$ExternalSyntheticLambda5
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationAccessDetails.lambda$disable$3(PreferenceScreen.this, (AbstractPreferenceController) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$disable$3(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof TypeFilterPreferenceController) {
            TypeFilterPreferenceController typeFilterPreferenceController = (TypeFilterPreferenceController) abstractPreferenceController;
            typeFilterPreferenceController.updateState(preferenceScreen.findPreference(typeFilterPreferenceController.getPreferenceKey()));
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void enable(ComponentName componentName) {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();
        ApprovalPreferenceController approvalPreferenceController = (ApprovalPreferenceController) use(ApprovalPreferenceController.class);
        approvalPreferenceController.enable(componentName);
        approvalPreferenceController.updateState(preferenceScreen.findPreference(approvalPreferenceController.getPreferenceKey()));
        getPreferenceControllers().forEach(new Consumer() { // from class: com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails$$ExternalSyntheticLambda4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationAccessDetails.lambda$enable$6(PreferenceScreen.this, (List) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$enable$6(final PreferenceScreen preferenceScreen, List list) {
        list.forEach(new Consumer() { // from class: com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails$$ExternalSyntheticLambda6
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NotificationAccessDetails.lambda$enable$5(PreferenceScreen.this, (AbstractPreferenceController) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$enable$5(PreferenceScreen preferenceScreen, AbstractPreferenceController abstractPreferenceController) {
        if (abstractPreferenceController instanceof TypeFilterPreferenceController) {
            TypeFilterPreferenceController typeFilterPreferenceController = (TypeFilterPreferenceController) abstractPreferenceController;
            typeFilterPreferenceController.updateState(preferenceScreen.findPreference(typeFilterPreferenceController.getPreferenceKey()));
        }
    }

    protected void loadNotificationListenerService() {
        this.mIsNls = false;
        if (this.mComponentName == null) {
            return;
        }
        for (ResolveInfo resolveInfo : this.mPm.queryIntentServicesAsUser(new Intent("android.service.notification.NotificationListenerService").setComponent(this.mComponentName), 132, this.mUserId)) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if ("android.permission.BIND_NOTIFICATION_LISTENER_SERVICE".equals(serviceInfo.permission) && Objects.equals(this.mComponentName, serviceInfo.getComponentName())) {
                this.mIsNls = true;
                this.mServiceName = serviceInfo.loadLabel(this.mPm);
                this.mServiceInfo = serviceInfo;
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.notification_access_permission_details;
    }
}
