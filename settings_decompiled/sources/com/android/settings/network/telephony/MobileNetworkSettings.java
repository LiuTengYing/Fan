package com.android.settings.network.telephony;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.UserManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.Settings;
import com.android.settings.SettingsActivity;
import com.android.settings.datausage.BillingCyclePreferenceController;
import com.android.settings.datausage.DataUsageSummaryPreferenceController;
import com.android.settings.network.ActiveSubscriptionsListener;
import com.android.settings.network.CarrierWifiTogglePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.cdma.CdmaSubscriptionPreferenceController;
import com.android.settings.network.telephony.cdma.CdmaSystemSelectPreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.NetworkSelectWarningDialogFragment;
import com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController;
import com.android.settings.network.telephony.gsm.UplmnPreferenceController;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.utils.ThreadUtils;
import com.unisoc.settings.network.UniEnabledNetworkModePreferenceController;
import com.unisoc.settings.network.telephony.UniMobileNetworkSwitchController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class MobileNetworkSettings extends AbstractMobileNetworkSettings {
    static final String KEY_CLICKED_PREF = "key_clicked_pref";
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.mobile_network_settings) { // from class: com.android.settings.network.telephony.MobileNetworkSettings.2
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ((UserManager) context.getSystemService(UserManager.class)).isAdminUser() && SubscriptionManager.isValidSubscriptionId(MobileNetworkUtils.getSearchableSubscriptionId(context));
        }
    };
    private ActiveSubscriptionsListener mActiveSubscriptionsListener;
    private int mActiveSubscriptionsListenerCount;
    private AutoSelectPreferenceController mAutoSelectPreferenceController;
    private CdmaSubscriptionPreferenceController mCdmaSubscriptionPreferenceController;
    private CdmaSystemSelectPreferenceController mCdmaSystemSelectPreferenceController;
    private String mClickedPrefKey;
    private boolean mDropFirstSubscriptionChangeNotify;
    private List<onManualNetworkSelectDoneListener> mListeners;
    private OpenNetworkSelectPagePreferenceController mOpenNetworkSelectPagePreferenceController;
    private int mSubId;
    private TelephonyManager mTelephonyManager;
    private UserManager mUserManager;

    /* loaded from: classes.dex */
    public interface onManualNetworkSelectDoneListener {
        void onManualNetworkSelectDone(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NetworkSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1571;
    }

    @Override // com.android.settings.network.telephony.AbstractMobileNetworkSettings, com.android.settings.dashboard.DashboardFragment, androidx.preference.PreferenceGroup.OnExpandButtonClickListener
    public /* bridge */ /* synthetic */ void onExpandButtonClick() {
        super.onExpandButtonClick();
    }

    public MobileNetworkSettings() {
        super("no_config_mobile_networks");
        this.mSubId = -1;
        this.mListeners = new ArrayList();
        this.mAutoSelectPreferenceController = null;
        this.mOpenNetworkSelectPagePreferenceController = null;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if (super.onPreferenceTreeClick(preference)) {
            return true;
        }
        String key = preference.getKey();
        if (TextUtils.equals(key, "cdma_system_select_key") || TextUtils.equals(key, "cdma_subscription_key")) {
            if (this.mTelephonyManager.getEmergencyCallbackMode()) {
                startActivityForResult(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null), 17);
                this.mClickedPrefKey = key;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        if (getArguments() == null) {
            Intent intent = getIntent();
            if (intent != null) {
                this.mSubId = intent.getIntExtra("android.provider.extra.SUB_ID", MobileNetworkUtils.getSearchableSubscriptionId(context));
                Log.d("NetworkSettings", "display subId from intent: " + this.mSubId);
            } else {
                Log.d("NetworkSettings", "intent is null, can not get the subId from intent.");
            }
        } else {
            this.mSubId = getArguments().getInt("android.provider.extra.SUB_ID", MobileNetworkUtils.getSearchableSubscriptionId(context));
            Log.d("NetworkSettings", "display subId from getArguments(): " + this.mSubId);
        }
        if (!SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            return Arrays.asList(new AbstractPreferenceController[0]);
        }
        return Arrays.asList(new DataUsageSummaryPreferenceController(getActivity(), getSettingsLifecycle(), this, this.mSubId));
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        Intent intent = getIntent();
        SubscriptionInfo subscriptionOrDefault = SubscriptionUtil.getSubscriptionOrDefault(context, this.mSubId);
        if (subscriptionOrDefault == null) {
            Log.d("NetworkSettings", "Invalid subId request " + this.mSubId);
            return;
        }
        int i = this.mSubId;
        updateSubscriptions(subscriptionOrDefault);
        if (!Settings.MobileNetworkActivity.doesIntentContainOptInAction(intent)) {
            removeContactDiscoveryDialog(i);
        }
        if (Settings.MobileNetworkActivity.doesIntentContainOptInAction(intent)) {
            showContactDiscoveryDialog(SubscriptionUtil.getSubscriptionOrDefault(context, this.mSubId));
        }
        DataUsageSummaryPreferenceController dataUsageSummaryPreferenceController = (DataUsageSummaryPreferenceController) use(DataUsageSummaryPreferenceController.class);
        if (dataUsageSummaryPreferenceController != null) {
            dataUsageSummaryPreferenceController.init(this.mSubId);
        }
        ((UniMobileNetworkSwitchController) use(UniMobileNetworkSwitchController.class)).init(this.mSubId);
        ((CarrierSettingsVersionPreferenceController) use(CarrierSettingsVersionPreferenceController.class)).init(this.mSubId);
        ((BillingCyclePreferenceController) use(BillingCyclePreferenceController.class)).init(this.mSubId);
        ((MmsMessagePreferenceController) use(MmsMessagePreferenceController.class)).init(this.mSubId);
        ((DataDuringCallsPreferenceController) use(DataDuringCallsPreferenceController.class)).init(this.mSubId);
        ((DisabledSubscriptionController) use(DisabledSubscriptionController.class)).init(this.mSubId);
        ((DeleteSimProfilePreferenceController) use(DeleteSimProfilePreferenceController.class)).init(this.mSubId, this, 18);
        ((DisableSimFooterPreferenceController) use(DisableSimFooterPreferenceController.class)).init(this.mSubId);
        ((NrDisabledInDsdsFooterPreferenceController) use(NrDisabledInDsdsFooterPreferenceController.class)).init(this.mSubId);
        ((MobileDataPreferenceController) use(MobileDataPreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((MobileDataPreferenceController) use(MobileDataPreferenceController.class)).setWifiPickerTrackerHelper(new WifiPickerTrackerHelper(getSettingsLifecycle(), context, null));
        ((RoamingPreferenceController) use(RoamingPreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((ApnPreferenceController) use(ApnPreferenceController.class)).init(this.mSubId);
        ((CarrierPreferenceController) use(CarrierPreferenceController.class)).init(this.mSubId);
        ((DataUsagePreferenceController) use(DataUsagePreferenceController.class)).init(this.mSubId);
        ((PreferredNetworkModePreferenceController) use(PreferredNetworkModePreferenceController.class)).init(this.mSubId);
        ((UniEnabledNetworkModePreferenceController) use(UniEnabledNetworkModePreferenceController.class)).init(this.mSubId);
        ((DataServiceSetupPreferenceController) use(DataServiceSetupPreferenceController.class)).init(this.mSubId);
        ((Enable2gPreferenceController) use(Enable2gPreferenceController.class)).init(this.mSubId);
        ((CarrierWifiTogglePreferenceController) use(CarrierWifiTogglePreferenceController.class)).init(getLifecycle(), this.mSubId);
        WifiCallingPreferenceController init = ((WifiCallingPreferenceController) use(WifiCallingPreferenceController.class)).init(this.mSubId);
        this.mOpenNetworkSelectPagePreferenceController = ((OpenNetworkSelectPagePreferenceController) use(OpenNetworkSelectPagePreferenceController.class)).initEx(getFragmentManager(), this, this.mSubId);
        AutoSelectPreferenceController autoSelectPreferenceController = (AutoSelectPreferenceController) use(AutoSelectPreferenceController.class);
        this.mAutoSelectPreferenceController = autoSelectPreferenceController;
        autoSelectPreferenceController.initEx(getFragmentManager(), this, this.mSubId);
        this.mAutoSelectPreferenceController.addListener(this.mOpenNetworkSelectPagePreferenceController);
        addManuallySelectDoneListener(this.mAutoSelectPreferenceController);
        ((NetworkPreferenceCategoryController) use(NetworkPreferenceCategoryController.class)).setChildren(Arrays.asList(this.mAutoSelectPreferenceController));
        CdmaSystemSelectPreferenceController cdmaSystemSelectPreferenceController = (CdmaSystemSelectPreferenceController) use(CdmaSystemSelectPreferenceController.class);
        this.mCdmaSystemSelectPreferenceController = cdmaSystemSelectPreferenceController;
        cdmaSystemSelectPreferenceController.init(getPreferenceManager(), this.mSubId);
        CdmaSubscriptionPreferenceController cdmaSubscriptionPreferenceController = (CdmaSubscriptionPreferenceController) use(CdmaSubscriptionPreferenceController.class);
        this.mCdmaSubscriptionPreferenceController = cdmaSubscriptionPreferenceController;
        cdmaSubscriptionPreferenceController.init(getPreferenceManager(), this.mSubId);
        VideoCallingPreferenceController init2 = ((VideoCallingPreferenceController) use(VideoCallingPreferenceController.class)).init(this.mSubId);
        VideoResolutionPreferenceController init3 = ((VideoResolutionPreferenceController) use(VideoResolutionPreferenceController.class)).init(this.mSubId);
        ArrayList arrayList = new ArrayList();
        arrayList.add(init2);
        arrayList.add(init3);
        ((CallingPreferenceCategoryController) use(CallingPreferenceCategoryController.class)).setChildren(Arrays.asList(init, init2, init3, ((BackupCallingPreferenceController) use(BackupCallingPreferenceController.class)).init(this.mSubId)));
        ((Enhanced4gLtePreferenceController) use(Enhanced4gLtePreferenceController.class)).init(this.mSubId).addListeners(arrayList);
        ((Enhanced4gCallingPreferenceController) use(Enhanced4gCallingPreferenceController.class)).init(this.mSubId).addListeners(arrayList);
        ((Enhanced4gAdvancedCallingPreferenceController) use(Enhanced4gAdvancedCallingPreferenceController.class)).init(this.mSubId).addListener(init2);
        ((ContactDiscoveryPreferenceController) use(ContactDiscoveryPreferenceController.class)).init(getParentFragmentManager(), this.mSubId);
        ((NrAdvancedCallingPreferenceController) use(NrAdvancedCallingPreferenceController.class)).init(this.mSubId);
        ((MobileDataAlwaysOnlinePreferenceController) use(MobileDataAlwaysOnlinePreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((UplmnPreferenceController) use(UplmnPreferenceController.class)).init(this.mSubId);
        ((PsDataOffPreferenceController) use(PsDataOffPreferenceController.class)).init(getFragmentManager(), this.mSubId);
        ((NationalRoamingPreferenceController) use(NationalRoamingPreferenceController.class)).init(this.mSubId);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        NetworkSelectWarningDialogFragment networkSelectWarningDialogFragment;
        Log.i("NetworkSettings", "onCreate:+");
        TelephonyStatusControlSession telephonyAvailabilityStatus = setTelephonyAvailabilityStatus(getPreferenceControllersAsList());
        super.onCreate(bundle);
        Context context = getContext();
        this.mUserManager = (UserManager) context.getSystemService("user");
        this.mTelephonyManager = ((TelephonyManager) context.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        telephonyAvailabilityStatus.close();
        if (bundle != null && (networkSelectWarningDialogFragment = (NetworkSelectWarningDialogFragment) getFragmentManager().findFragmentByTag("NetworkSelectWarningDialog")) != null) {
            NetworkSelectWarningDialogFragment.setParentFragment(this);
            AutoSelectPreferenceController autoSelectPreferenceController = this.mAutoSelectPreferenceController;
            if (autoSelectPreferenceController != null) {
                networkSelectWarningDialogFragment.registerForAutoSelect(autoSelectPreferenceController);
            }
            OpenNetworkSelectPagePreferenceController openNetworkSelectPagePreferenceController = this.mOpenNetworkSelectPagePreferenceController;
            if (openNetworkSelectPagePreferenceController != null) {
                networkSelectWarningDialogFragment.registerForOpenNetwork(openNetworkSelectPagePreferenceController);
            }
        }
        onRestoreInstance(bundle);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.d("NetworkSettings", "onResume() subId=" + this.mSubId);
        if (this.mActiveSubscriptionsListener == null) {
            this.mActiveSubscriptionsListener = new ActiveSubscriptionsListener(getContext().getMainLooper(), getContext(), this.mSubId) { // from class: com.android.settings.network.telephony.MobileNetworkSettings.1
                @Override // com.android.settings.network.ActiveSubscriptionsListener
                public void onChanged() {
                    MobileNetworkSettings.this.onSubscriptionDetailChanged();
                }
            };
            this.mDropFirstSubscriptionChangeNotify = true;
        }
        this.mActiveSubscriptionsListener.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSubscriptionDetailChanged() {
        if (this.mDropFirstSubscriptionChangeNotify) {
            this.mDropFirstSubscriptionChangeNotify = false;
            Log.d("NetworkSettings", "Callback during onResume()");
            return;
        }
        final SubscriptionInfo subscriptionOrDefault = SubscriptionUtil.getSubscriptionOrDefault(getContext(), this.mSubId);
        if (subscriptionOrDefault != null) {
            final Consumer consumer = new Consumer() { // from class: com.android.settings.network.telephony.MobileNetworkSettings$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    MobileNetworkSettings.lambda$onSubscriptionDetailChanged$0(subscriptionOrDefault, (Activity) obj);
                }
            };
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.telephony.MobileNetworkSettings$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    MobileNetworkSettings.this.lambda$onSubscriptionDetailChanged$1(consumer);
                }
            });
        }
        int i = this.mActiveSubscriptionsListenerCount + 1;
        this.mActiveSubscriptionsListenerCount = i;
        if (i != 1) {
            return;
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.telephony.MobileNetworkSettings$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                MobileNetworkSettings.this.lambda$onSubscriptionDetailChanged$2(subscriptionOrDefault);
            }
        });
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Log.d("NetworkSettings", "onSubscriptionDetailChanged supportInvalidateOptionsMenu");
            activity.supportInvalidateOptionsMenu();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$onSubscriptionDetailChanged$0(SubscriptionInfo subscriptionInfo, Activity activity) {
        if (activity == null || activity.isFinishing() || !(activity instanceof SettingsActivity)) {
            return;
        }
        ((SettingsActivity) activity).setTitle(SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, activity));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubscriptionDetailChanged$1(Consumer consumer) {
        consumer.accept(getActivity());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSubscriptionDetailChanged$2(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            finishFragment();
            return;
        }
        this.mActiveSubscriptionsListenerCount = 0;
        redrawPreferenceControllers();
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onDestroy() {
        ActiveSubscriptionsListener activeSubscriptionsListener = this.mActiveSubscriptionsListener;
        if (activeSubscriptionsListener != null) {
            activeSubscriptionsListener.stop();
        }
        super.onDestroy();
    }

    void onRestoreInstance(Bundle bundle) {
        if (bundle != null) {
            this.mClickedPrefKey = bundle.getString(KEY_CLICKED_PREF);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.mobile_network_settings;
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(KEY_CLICKED_PREF, this.mClickedPrefKey);
    }

    @Override // com.android.settings.dashboard.RestrictedDashboardFragment, androidx.fragment.app.Fragment
    public void onActivityResult(int i, int i2, Intent intent) {
        Preference findPreference;
        FragmentActivity activity;
        if (i == 17) {
            if (i2 == 0 || (findPreference = getPreferenceScreen().findPreference(this.mClickedPrefKey)) == null) {
                return;
            }
            findPreference.performClick();
        } else if (i == 18) {
            if (i2 == 0 || (activity = getActivity()) == null || activity.isFinishing()) {
                return;
            }
            activity.finish();
        } else if (i == 100) {
            Log.i("NetworkSettings", "REQUEST_MANUALLY_NETWORK_SELECTION_DONE");
            if (intent != null) {
                boolean booleanExtra = intent.getBooleanExtra("manual_select_success", true);
                Log.i("NetworkSettings", "network selection manually done: " + booleanExtra);
                for (onManualNetworkSelectDoneListener onmanualnetworkselectdonelistener : this.mListeners) {
                    onmanualnetworkselectdonelistener.onManualNetworkSelectDone(booleanExtra);
                }
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem findItem = menu.findItem(R$id.edit_sim_name);
        if (findItem != null && !SubscriptionManager.isValidPhoneId(SubscriptionManager.getPhoneId(this.mSubId))) {
            findItem.setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId)) {
            MenuItem add = menu.add(0, R$id.edit_sim_name, 0, R$string.mobile_network_sim_name);
            add.setIcon(17302778);
            add.setShowAsAction(2);
        }
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (SubscriptionManager.isValidSubscriptionId(this.mSubId) && menuItem.getItemId() == R$id.edit_sim_name) {
            RenameMobileNetworkDialogFragment newInstance = RenameMobileNetworkDialogFragment.newInstance(this.mSubId);
            if (newInstance.getDialog() == null || !newInstance.getDialog().isShowing()) {
                newInstance.show(getFragmentManager(), "RenameMobileNetwork");
                return true;
            }
            return false;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private ContactDiscoveryDialogFragment getContactDiscoveryFragment(int i) {
        return (ContactDiscoveryDialogFragment) getChildFragmentManager().findFragmentByTag(ContactDiscoveryDialogFragment.getFragmentTag(i));
    }

    private void removeContactDiscoveryDialog(int i) {
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(i);
        if (contactDiscoveryFragment != null) {
            contactDiscoveryFragment.dismiss();
        }
    }

    private void showContactDiscoveryDialog(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            Log.d("NetworkSettings", "Invalid subId request " + this.mSubId);
            onDestroy();
            return;
        }
        CharSequence uniqueSubscriptionDisplayName = SubscriptionUtil.getUniqueSubscriptionDisplayName(subscriptionInfo, getContext());
        ContactDiscoveryDialogFragment contactDiscoveryFragment = getContactDiscoveryFragment(this.mSubId);
        if (contactDiscoveryFragment == null) {
            contactDiscoveryFragment = ContactDiscoveryDialogFragment.newInstance(this.mSubId, uniqueSubscriptionDisplayName);
        }
        if (contactDiscoveryFragment.isAdded()) {
            return;
        }
        contactDiscoveryFragment.show(getChildFragmentManager(), ContactDiscoveryDialogFragment.getFragmentTag(this.mSubId));
    }

    private void updateSubscriptions(SubscriptionInfo subscriptionInfo) {
        if (subscriptionInfo == null) {
            return;
        }
        this.mSubId = subscriptionInfo.getSubscriptionId();
    }

    public void addManuallySelectDoneListener(onManualNetworkSelectDoneListener onmanualnetworkselectdonelistener) {
        this.mListeners.add(onmanualnetworkselectdonelistener);
    }
}
