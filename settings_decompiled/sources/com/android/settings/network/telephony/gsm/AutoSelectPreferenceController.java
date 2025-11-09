package com.android.settings.network.telephony.gsm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.AllowedNetworkTypesListener;
import com.android.settings.network.CarrierConfigCache;
import com.android.settings.network.telephony.MobileNetworkSettings;
import com.android.settings.network.telephony.MobileNetworkUtils;
import com.android.settings.network.telephony.NetworkSelectSettings;
import com.android.settings.network.telephony.TelephonyTogglePreferenceController;
import com.android.settings.network.telephony.gsm.NetworkSelectWarningDialogFragment;
import com.android.settingslib.utils.ThreadUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class AutoSelectPreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, MobileNetworkSettings.onManualNetworkSelectDoneListener, NetworkSelectWarningDialogFragment.WarningDialogListener {
    private static final long MINIMUM_DIALOG_TIME_MILLIS = TimeUnit.SECONDS.toMillis(1);
    private static final String TAG = "AutoSelectPreferenceController";
    private AllowedNetworkTypesListener mAllowedNetworkTypesListener;
    private int mCacheOfModeStatus;
    private FragmentManager mFragmentManager;
    private List<OnNetworkSelectModeListener> mListeners;
    private boolean mOnlyAutoSelectInHome;
    private Fragment mParentFragment;
    private PreferenceScreen mPreferenceScreen;
    ProgressDialog mProgressDialog;
    private AtomicLong mRecursiveUpdate;
    SwitchPreference mSwitchPreference;
    private TelephonyManager mTelephonyManager;
    private final Handler mUiHandler;
    private AtomicBoolean mUpdatingConfig;

    /* loaded from: classes.dex */
    public interface OnNetworkSelectModeListener {
        void onNetworkSelectModeUpdated(int i);
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AutoSelectPreferenceController(Context context, String str) {
        super(context, str);
        this.mTelephonyManager = (TelephonyManager) context.getSystemService(TelephonyManager.class);
        this.mSubId = -1;
        this.mRecursiveUpdate = new AtomicLong();
        this.mUpdatingConfig = new AtomicBoolean();
        this.mCacheOfModeStatus = 0;
        this.mListeners = new ArrayList();
        Handler handler = new Handler(Looper.getMainLooper());
        this.mUiHandler = handler;
        AllowedNetworkTypesListener allowedNetworkTypesListener = new AllowedNetworkTypesListener(new HandlerExecutor(handler));
        this.mAllowedNetworkTypesListener = allowedNetworkTypesListener;
        allowedNetworkTypesListener.setAllowedNetworkTypesListener(new AllowedNetworkTypesListener.OnAllowedNetworkTypesListener() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda2
            @Override // com.android.settings.network.AllowedNetworkTypesListener.OnAllowedNetworkTypesListener
            public final void onAllowedNetworkTypesChanged() {
                AutoSelectPreferenceController.this.lambda$new$0();
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
        if (this.mSwitchPreference != null) {
            this.mRecursiveUpdate.getAndIncrement();
            updateState(this.mSwitchPreference);
            this.mRecursiveUpdate.decrementAndGet();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mAllowedNetworkTypesListener.register(this.mContext, this.mSubId);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mAllowedNetworkTypesListener.unregister(this.mContext, this.mSubId);
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        return MobileNetworkUtils.shouldDisplayNetworkSelectOptions(this.mContext, i) ? 0 : 2;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        if (!this.mUpdatingConfig.get()) {
            this.mCacheOfModeStatus = this.mTelephonyManager.getNetworkSelectionMode();
            for (OnNetworkSelectModeListener onNetworkSelectModeListener : this.mListeners) {
                onNetworkSelectModeListener.onNetworkSelectModeUpdated(this.mCacheOfModeStatus);
            }
        }
        return this.mCacheOfModeStatus == 1;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(final Preference preference) {
        super.updateState(null);
        ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AutoSelectPreferenceController.this.lambda$updateState$2(preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$2(final Preference preference) {
        final boolean z = this.mTelephonyManager.getNetworkSelectionMode() == 1;
        this.mUiHandler.post(new Runnable() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AutoSelectPreferenceController.this.lambda$updateState$1(z, preference);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateState$1(boolean z, Preference preference) {
        this.mSwitchPreference.setChecked(z);
        preference.setSummary((CharSequence) null);
        ServiceState serviceState = this.mTelephonyManager.getServiceState();
        if (serviceState == null) {
            preference.setEnabled(false);
        } else if (serviceState.getRoaming()) {
            preference.setEnabled(true);
        } else {
            preference.setEnabled(!this.mOnlyAutoSelectInHome);
            if (this.mOnlyAutoSelectInHome) {
                preference.setSummary(this.mContext.getString(R$string.manual_mode_disallowed_summary, this.mTelephonyManager.getSimOperatorName()));
            }
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (this.mRecursiveUpdate.get() != MINIMUM_DIALOG_TIME_MILLIS) {
            return true;
        }
        if (!MobileNetworkUtils.isNetworkSelectEnabled(this.mContext)) {
            this.mSwitchPreference.setChecked(!z);
            return false;
        } else if (z) {
            setAutomaticSelectionMode();
            return false;
        } else {
            boolean z2 = this.mContext.getResources().getBoolean(R$bool.config_show_warning_dialog_manual_search);
            Log.d(TAG, "setChecked, isShowDialog = " + z2);
            if (!z2) {
                Bundle bundle = new Bundle();
                bundle.putInt("android.provider.extra.SUB_ID", this.mSubId);
                new SubSettingLauncher(this.mContext).setDestination(NetworkSelectSettings.class.getName()).setSourceMetricsCategory(1581).setTitleRes(R$string.choose_network_title).setArguments(bundle).setResultListener(this.mParentFragment, 100).launch();
            } else {
                this.mSwitchPreference.setEnabled(false);
                showWarningDialog();
            }
            return false;
        }
    }

    Future setAutomaticSelectionMode() {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        showAutoSelectProgressBar();
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (switchPreference != null) {
            switchPreference.setIntent(null);
            this.mSwitchPreference.setEnabled(false);
        }
        return ThreadUtils.postOnBackgroundThread(new Runnable() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AutoSelectPreferenceController.this.lambda$setAutomaticSelectionMode$4(elapsedRealtime);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAutomaticSelectionMode$4(long j) {
        this.mUpdatingConfig.set(true);
        Log.d(TAG, "select network automatically...");
        this.mTelephonyManager.setNetworkSelectionModeAutomatic();
        this.mUpdatingConfig.set(false);
        this.mUiHandler.postDelayed(new Runnable() { // from class: com.android.settings.network.telephony.gsm.AutoSelectPreferenceController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                AutoSelectPreferenceController.this.lambda$setAutomaticSelectionMode$3();
            }
        }, Math.max(MINIMUM_DIALOG_TIME_MILLIS - (SystemClock.elapsedRealtime() - j), (long) MINIMUM_DIALOG_TIME_MILLIS));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setAutomaticSelectionMode$3() {
        this.mRecursiveUpdate.getAndIncrement();
        this.mSwitchPreference.setEnabled(true);
        this.mSwitchPreference.setChecked(isChecked());
        this.mRecursiveUpdate.decrementAndGet();
        dismissProgressBar();
    }

    public AutoSelectPreferenceController init(int i) {
        this.mSubId = i;
        this.mTelephonyManager = ((TelephonyManager) this.mContext.getSystemService(TelephonyManager.class)).createForSubscriptionId(this.mSubId);
        PersistableBundle configForSubId = CarrierConfigCache.getInstance(this.mContext).getConfigForSubId(this.mSubId);
        this.mOnlyAutoSelectInHome = configForSubId != null ? configForSubId.getBoolean("only_auto_select_in_home_network") : false;
        return this;
    }

    public AutoSelectPreferenceController addListener(OnNetworkSelectModeListener onNetworkSelectModeListener) {
        this.mListeners.add(onNetworkSelectModeListener);
        return this;
    }

    private void showAutoSelectProgressBar() {
        if (this.mProgressDialog == null) {
            ProgressDialog progressDialog = new ProgressDialog(this.mContext);
            this.mProgressDialog = progressDialog;
            progressDialog.setMessage(this.mContext.getResources().getString(R$string.register_automatically));
            this.mProgressDialog.setCanceledOnTouchOutside(false);
            this.mProgressDialog.setCancelable(false);
            this.mProgressDialog.setIndeterminate(true);
        }
        this.mProgressDialog.show();
    }

    private void dismissProgressBar() {
        ProgressDialog progressDialog = this.mProgressDialog;
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        try {
            this.mProgressDialog.dismiss();
        } catch (IllegalArgumentException unused) {
        }
    }

    public AutoSelectPreferenceController initEx(FragmentManager fragmentManager, Fragment fragment, int i) {
        this.mSubId = i;
        this.mTelephonyManager = TelephonyManager.from(this.mContext).createForSubscriptionId(this.mSubId);
        PersistableBundle configForSubId = CarrierConfigCache.getInstance(this.mContext).getConfigForSubId(this.mSubId);
        this.mOnlyAutoSelectInHome = configForSubId != null ? configForSubId.getBoolean("only_auto_select_in_home_network") : false;
        this.mFragmentManager = fragmentManager;
        this.mParentFragment = fragment;
        return this;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        dismissProgressBar();
    }

    private void showWarningDialog() {
        Log.d(TAG, "showWarningDialog");
        NetworkSelectWarningDialogFragment newInstance = NetworkSelectWarningDialogFragment.newInstance(this.mParentFragment, this.mSubId);
        newInstance.registerForAutoSelect(this);
        newInstance.show(this.mFragmentManager, "NetworkSelectWarningDialog");
    }

    @Override // com.android.settings.network.telephony.MobileNetworkSettings.onManualNetworkSelectDoneListener
    public void onManualNetworkSelectDone(boolean z) {
        Log.d(TAG, "onManualNetworkSelectDone: " + z);
        if (!z) {
            setChecked(true);
        } else {
            this.mSwitchPreference.setChecked(false);
        }
    }

    @Override // com.android.settings.network.telephony.gsm.NetworkSelectWarningDialogFragment.WarningDialogListener
    public void onDialogDismiss(InstrumentedDialogFragment instrumentedDialogFragment) {
        SwitchPreference switchPreference = this.mSwitchPreference;
        if (switchPreference != null) {
            switchPreference.setEnabled(true);
        }
    }
}
