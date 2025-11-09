package com.android.settings.network.telephony.gsm;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkSelectSettings;
import com.android.settings.network.telephony.TelephonyBasePreferenceController;
import com.android.settings.network.telephony.gsm.AutoSelectPreferenceController;
import com.android.settings.network.telephony.gsm.NetworkSelectWarningDialogFragment;
import com.android.settingslib.utils.ThreadUtils;
/* loaded from: classes.dex */
public class OpenNetworkSelectPagePreferenceController extends TelephonyBasePreferenceController implements AutoSelectPreferenceController.OnNetworkSelectModeListener, LifecycleObserver, NetworkSelectWarningDialogFragment.WarningDialogListener {
    private static final String TAG = "OpenNetworkSelectPagePreferenceController";
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private int mCacheOfModeStatus;
    private FragmentManager mFragmentManager;
    private Fragment mParentFragment;
    private PhoneStateListener mPhoneStateListener;
    private Preference mPreference;
    private PreferenceScreen mPreferenceScreen;
    private TelephonyManager mTelephonyManager;

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public OpenNetworkSelectPagePreferenceController(Context context, String str) {
        super(context, str);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubId = -1;
        this.mCacheOfModeStatus = 0;
        AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(context.getMainExecutor());
        this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
        allowedNetworkTypesListener.setAllowedNetworkTypesListener(new AllowedNetworkTypesListener.OnAllowedNetworkTypesListener() { // from class: com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController$$ExternalSyntheticLambda1
            @Override // com.android.settings.network.AllowedNetworkTypesListener.OnAllowedNetworkTypesListener
            public final void onAllowedNetworkTypesChanged() {
                OpenNetworkSelectPagePreferenceController.this.lambda$new$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: updatePreference */
    public void lambda$new$0() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    @Override // com.android.settings.network.telephony.TelephonyBasePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return MobileNetworkUtils.shouldDisplayNetworkSelectOptions(this.mContext, i) ? 0 : 2;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mAllowedNetworkTypesListener.register(this.mContext, this.mSubId);
        if (isAvailable()) {
            if (this.mPhoneStateListener == null) {
                this.mPhoneStateListener = new PhoneStateListener() { // from class: com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController.1
                    @Override // android.telephony.PhoneStateListener
                    public void onServiceStateChanged(ServiceState serviceState) {
                        OpenNetworkSelectPagePreferenceController openNetworkSelectPagePreferenceController = OpenNetworkSelectPagePreferenceController.this;
                        openNetworkSelectPagePreferenceController.updateState(openNetworkSelectPagePreferenceController.mPreference);
                    }
                };
            }
            this.mTelephonyManager.listen(this.mPhoneStateListener, 1);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mAllowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
        PhoneStateListener phoneStateListener = this.mPhoneStateListener;
        if (phoneStateListener != null) {
            this.mTelephonyManager.listen(phoneStateListener, 0);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        super.updateState(preference);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                OpenNetworkSelectPagePreferenceController.this.lambda$updateState$2(preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$2(final Preference preference) {
        final int networkSelectionMode = this.mTelephonyManager.getNetworkSelectionMode();
        new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.android.settings.network.telephony.gsm.OpenNetworkSelectPagePreferenceController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                OpenNetworkSelectPagePreferenceController.lambda$updateState$1(Preference.this, networkSelectionMode);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void lambda$updateState$1(Preference preference, int i) {
        preference.setEnabled(i != 1);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        if (serviceState != null && (serviceState.getState() == 0 || serviceState.getDataRegState() == 0)) {
            return MobileNetworkUtils.getCurrentCarrierNameForDisplay(this.mContext, this.mSubId);
        }
        return this.mContext.getString(R$string.network_disconnected);
    }

    public OpenNetworkSelectPagePreferenceController init(int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        return this;
    }

    @Override // com.android.settings.network.telephony.gsm.AutoSelectPreferenceController.OnNetworkSelectModeListener
    public void onNetworkSelectModeUpdated(int i) {
        this.mCacheOfModeStatus = i;
        Preference preference = this.mPreference;
        if (preference != null) {
            updateState(preference);
        }
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        boolean z = this.mContext.getResources().getBoolean(R$bool.config_show_warning_dialog_manual_search);
        Log.d(TAG, "isShowDialog = " + z);
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            if (!z) {
                Bundle bundle = new Bundle();
                bundle.putInt("android.provider.extra.SUB_ID", this.mSubId);
                new SubSettingLauncher(this.mContext).setDestination(NetworkSelectSettings.class.getName()).setSourceMetricsCategory(1581).setTitleRes(R$string.choose_network_title).setArguments(bundle).setResultListener(this.mParentFragment, 100).launch();
            } else {
                this.mPreference.setEnabled(false);
                if (!MobileNetworkUtils.isNetworkSelectEnabled(this.mContext)) {
                    this.mPreference.setEnabled(true);
                    return false;
                }
                showWarningDialog();
            }
            return true;
        }
        return false;
    }

    public OpenNetworkSelectPagePreferenceController initEx(FragmentManager fragmentManager, Fragment fragment, int i) {
        this.mFragmentManager = fragmentManager;
        this.mParentFragment = fragment;
        this.mSubId = i;
        this.mTelephonyManager = TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId);
        return this;
    }

    private void showWarningDialog() {
        Log.d(TAG, "showWarningDialog");
        NetworkSelectWarningDialogFragment newInstance = NetworkSelectWarningDialogFragment.newInstance(this.mParentFragment, this.mSubId);
        newInstance.registerForOpenNetwork(this);
        newInstance.show(this.mFragmentManager, "NetworkSelectWarningDialog");
    }

    @Override // com.android.settings.network.telephony.gsm.NetworkSelectWarningDialogFragment.WarningDialogListener
    public void onDialogDismiss(InstrumentedDialogFragment instrumentedDialogFragment) {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setEnabled(true);
        }
    }
}
