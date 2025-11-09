package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.internal.telephony.GlobalSettingsHelper;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.instrumentation.InstrumentedDialogFragment;
import com.android.settings.network.telephony.MobileDataAlwaysOnlineDialogFragment;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.unisoc.settings.network.telephony.UniMobileNetworkUtils;
/* loaded from: classes.dex */
public class MobileDataAlwaysOnlinePreferenceController extends TelephonyTogglePreferenceController implements LifecycleObserver, OnStart, OnStop, MobileDataAlwaysOnlineDialogFragment.MobileDataAlwaysOnlineDialogListener {
    private static final String DIALOG_TAG = "MobileDataAlwaysOnlineDialog";
    private static final String LOG_TAG = "MobileDataAlwaysOnlinePreferenceController";
    private MobileDataAlwaysOnlineContentObserver mDataAlwaysOnlineContentObserver;
    FragmentManager mFragmentManager;
    boolean mNeedDialog;
    private SwitchPreference mSwitchPreference;

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

    public MobileDataAlwaysOnlinePreferenceController(Context context, String str) {
        super(context, str);
        this.mDataAlwaysOnlineContentObserver = new MobileDataAlwaysOnlineContentObserver(new Handler(Looper.getMainLooper()));
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mDataAlwaysOnlineContentObserver.register(this.mContext, this.mSubId);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mDataAlwaysOnlineContentObserver.unRegister(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mSwitchPreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.network.telephony.TelephonyTogglePreferenceController, com.android.settings.network.telephony.TelephonyAvailabilityCallback
    public int getAvailabilityStatus(int i) {
        if (SubscriptionManager.isValidSubscriptionId(i)) {
            return this.mContext.getResources().getBoolean(R$bool.enable_mobile_data_always_online) ? 0 : 4;
        }
        return 1;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            if (this.mNeedDialog) {
                showDialog();
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        boolean z2 = !z;
        this.mNeedDialog = z2;
        if (z2) {
            return false;
        }
        setMobileDataAlwaysOnline(this.mSubId, true);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return isMobileDataAlwaysOnline(this.mSubId);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setSummary(R$string.mobile_data_always_online_summary);
        if (UniMobileNetworkUtils.isPrefEnabledForSubsidy(this.mContext, this.mSubId)) {
            return;
        }
        this.mSwitchPreference.setEnabled(false);
    }

    public void init(FragmentManager fragmentManager, int i) {
        this.mFragmentManager = fragmentManager;
        this.mSubId = i;
    }

    private void showDialog() {
        this.mSwitchPreference.setEnabled(false);
        MobileDataAlwaysOnlineDialogFragment newInstance = MobileDataAlwaysOnlineDialogFragment.newInstance(this.mSubId);
        newInstance.setController(this);
        newInstance.show(this.mFragmentManager, DIALOG_TAG);
    }

    @Override // com.android.settings.network.telephony.MobileDataAlwaysOnlineDialogFragment.MobileDataAlwaysOnlineDialogListener
    public void onDialogDismiss(InstrumentedDialogFragment instrumentedDialogFragment) {
        Log.d(LOG_TAG, "onDialogDismiss");
        this.mSwitchPreference.setEnabled(true);
    }

    private boolean isMobileDataAlwaysOnline(int i) {
        return 1 == GlobalSettingsHelper.getInt(this.mContext, "mobile_data_always_online", i, 1);
    }

    private void setMobileDataAlwaysOnline(int i, boolean z) {
        GlobalSettingsHelper.setInt(this.mContext, "mobile_data_always_online", i, z ? 1 : 0);
    }

    /* loaded from: classes.dex */
    public class MobileDataAlwaysOnlineContentObserver extends ContentObserver {
        public MobileDataAlwaysOnlineContentObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            MobileDataAlwaysOnlinePreferenceController mobileDataAlwaysOnlinePreferenceController = MobileDataAlwaysOnlinePreferenceController.this;
            mobileDataAlwaysOnlinePreferenceController.updateState(mobileDataAlwaysOnlinePreferenceController.mSwitchPreference);
        }

        public void register(Context context, int i) {
            Uri uriFor;
            if (TelephonyManager.getDefault().getSimCount() == 1) {
                uriFor = Settings.Global.getUriFor("mobile_data_always_online");
            } else {
                uriFor = Settings.Global.getUriFor("mobile_data_always_online" + i);
            }
            context.getContentResolver().registerContentObserver(uriFor, false, this);
        }

        public void unRegister(Context context) {
            context.getContentResolver().unregisterContentObserver(this);
        }
    }
}
