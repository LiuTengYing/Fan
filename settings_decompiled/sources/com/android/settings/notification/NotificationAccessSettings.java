package com.android.settings.notification;

import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.companion.ICompanionDeviceManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Utils;
import com.android.settings.applications.specialaccess.notificationaccess.NotificationAccessDetails;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.utils.ManagedServiceSettings;
import com.android.settings.widget.EmptyTextSettings;
import com.android.settingslib.applications.ServiceListing;
import com.android.settingslib.widget.AppPreference;
import java.util.List;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class NotificationAccessSettings extends EmptyTextSettings {
    private static final ManagedServiceSettings.Config CONFIG = new ManagedServiceSettings.Config.Builder().setTag("NotifAccessSettings").setSetting("enabled_notification_listeners").setIntentAction("android.service.notification.NotificationListenerService").setPermission("android.permission.BIND_NOTIFICATION_LISTENER_SERVICE").setNoun("notification listener").setWarningDialogTitle(R$string.notification_listener_security_warning_title).setWarningDialogSummary(R$string.notification_listener_security_warning_summary).setEmptyText(R$string.no_notification_listeners).build();
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.notification_access_settings);
    private NotificationBackend mBackend = new NotificationBackend();
    protected Context mContext;
    private DevicePolicyManager mDpm;
    private IconDrawableFactory mIconDrawableFactory;
    private NotificationManager mNm;
    private PackageManager mPm;
    private ServiceListing mServiceListing;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 179;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FragmentActivity activity = getActivity();
        this.mContext = activity;
        this.mPm = activity.getPackageManager();
        this.mDpm = (DevicePolicyManager) this.mContext.getSystemService("device_policy");
        this.mIconDrawableFactory = IconDrawableFactory.newInstance(this.mContext);
        ServiceListing.Builder builder = new ServiceListing.Builder(this.mContext);
        ManagedServiceSettings.Config config = CONFIG;
        ServiceListing build = builder.setPermission(config.permission).setIntentAction(config.intentAction).setNoun(config.noun).setSetting(config.setting).setTag(config.tag).build();
        this.mServiceListing = build;
        build.addCallback(new ServiceListing.Callback() { // from class: com.android.settings.notification.NotificationAccessSettings$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.applications.ServiceListing.Callback
            public final void onServicesReloaded(List list) {
                NotificationAccessSettings.this.updateList(list);
            }
        });
        if (UserManager.get(this.mContext).isManagedProfile()) {
            Toast.makeText(this.mContext, this.mDpm.getResources().getString("Settings.WORK_APPS_CANNOT_ACCESS_NOTIFICATION_SETTINGS", new Supplier() { // from class: com.android.settings.notification.NotificationAccessSettings$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$onCreate$0;
                    lambda$onCreate$0 = NotificationAccessSettings.this.lambda$onCreate$0();
                    return lambda$onCreate$0;
                }
            }), 0).show();
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$onCreate$0() {
        return this.mContext.getString(R$string.notification_settings_work_profile);
    }

    @Override // com.android.settings.widget.EmptyTextSettings, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        setEmptyText(CONFIG.emptyText);
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        this.mServiceListing.reload();
        this.mServiceListing.setListening(true);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mServiceListing.setListening(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateList(List<ServiceInfo> list) {
        int managedProfileId = Utils.getManagedProfileId((UserManager) this.mContext.getSystemService("user"), UserHandle.myUserId());
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference("allowed");
        preferenceCategory.removeAll();
        PreferenceCategory preferenceCategory2 = (PreferenceCategory) preferenceScreen.findPreference("not_allowed");
        preferenceCategory2.removeAll();
        list.sort(new PackageItemInfo.DisplayNameComparator(this.mPm));
        for (final ServiceInfo serviceInfo : list) {
            final ComponentName componentName = new ComponentName(serviceInfo.packageName, serviceInfo.name);
            CharSequence charSequence = null;
            try {
                charSequence = this.mPm.getApplicationInfoAsUser(serviceInfo.packageName, 0, UserHandle.myUserId()).loadLabel(this.mPm);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("NotifAccessSettings", "can't find package name", e);
            }
            AppPreference appPreference = new AppPreference(getPrefContext());
            appPreference.setTitle(charSequence);
            IconDrawableFactory iconDrawableFactory = this.mIconDrawableFactory;
            ApplicationInfo applicationInfo = serviceInfo.applicationInfo;
            appPreference.setIcon(iconDrawableFactory.getBadgedIcon(serviceInfo, applicationInfo, UserHandle.getUserId(applicationInfo.uid)));
            appPreference.setKey(componentName.flattenToString());
            appPreference.setSummary(NotificationBackend.getDeviceList(ICompanionDeviceManager.Stub.asInterface(ServiceManager.getService("companiondevice")), com.android.settings.bluetooth.Utils.getLocalBtManager(this.mContext), serviceInfo.packageName, UserHandle.myUserId()));
            if (managedProfileId != -10000 && !this.mDpm.isNotificationListenerServicePermitted(serviceInfo.packageName, managedProfileId)) {
                appPreference.setSummary(this.mDpm.getResources().getString("Settings.WORK_PROFILE_NOTIFICATION_LISTENER_BLOCKED", new Supplier() { // from class: com.android.settings.notification.NotificationAccessSettings$$ExternalSyntheticLambda2
                    @Override // java.util.function.Supplier
                    public final Object get() {
                        String lambda$updateList$1;
                        lambda$updateList$1 = NotificationAccessSettings.this.lambda$updateList$1();
                        return lambda$updateList$1;
                    }
                }));
            }
            appPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.notification.NotificationAccessSettings$$ExternalSyntheticLambda3
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$updateList$2;
                    lambda$updateList$2 = NotificationAccessSettings.this.lambda$updateList$2(componentName, serviceInfo, preference);
                    return lambda$updateList$2;
                }
            });
            appPreference.setKey(componentName.flattenToString());
            if (this.mNm.isNotificationListenerAccessGranted(componentName)) {
                preferenceCategory.addPreference(appPreference);
            } else {
                preferenceCategory2.addPreference(appPreference);
            }
        }
        highlightPreferenceIfNeeded();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateList$1() {
        return getString(R$string.work_profile_notification_access_blocked_summary);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateList$2(ComponentName componentName, ServiceInfo serviceInfo, Preference preference) {
        Bundle bundle = new Bundle();
        bundle.putString("package", componentName.getPackageName());
        bundle.putInt("uid", serviceInfo.applicationInfo.uid);
        Bundle bundle2 = new Bundle();
        bundle2.putString("android.provider.extra.NOTIFICATION_LISTENER_COMPONENT_NAME", componentName.flattenToString());
        new SubSettingLauncher(getContext()).setDestination(NotificationAccessDetails.class.getName()).setSourceMetricsCategory(getMetricsCategory()).setTitleRes(R$string.manage_notification_access_title).setArguments(bundle).setExtras(bundle2).setUserHandle(UserHandle.getUserHandleForUid(serviceInfo.applicationInfo.uid)).launch();
        return true;
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mNm = (NotificationManager) context.getSystemService(NotificationManager.class);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.notification_access_settings;
    }
}
