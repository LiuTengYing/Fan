package com.android.settings.network;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.telecom.TelecomManager;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.AirplaneModeEnabler;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class AirplaneModePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop, OnDestroy, AirplaneModeEnabler.OnAirplaneModeChangedListener {
    private static final String LOG_TAG = "AirplaneModePreferenceController";
    public static final int REQUEST_CODE_EXIT_ECM = 1;
    public static final Uri SLICE_URI = new Uri.Builder().scheme("content").authority("android.settings.slices").appendPath("action").appendPath("airplane_mode").build();
    public static final String TELECOM_SERVICE = "telecom";
    private AirplaneModeEnabler mAirplaneModeEnabler;
    private SwitchPreference mAirplaneModePreference;
    private Fragment mFragment;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public AirplaneModePreferenceController(Context context, String str) {
        super(context, str);
        if (isAvailable(this.mContext)) {
            this.mAirplaneModeEnabler = new AirplaneModeEnabler(this.mContext, this);
        }
    }

    public void setFragment(Fragment fragment) {
        this.mFragment = fragment;
    }

    void setAirplaneModeEnabler(AirplaneModeEnabler airplaneModeEnabler) {
        this.mAirplaneModeEnabler = airplaneModeEnabler;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if ("airplane_mode".equals(preference.getKey()) && this.mAirplaneModeEnabler.isInEcmMode()) {
            Fragment fragment = this.mFragment;
            if (fragment != null) {
                fragment.startActivityForResult(new Intent("android.telephony.action.SHOW_NOTICE_ECM_BLOCK_OTHERS", (Uri) null), 1);
            }
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public Uri getSliceUri() {
        return SLICE_URI;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mAirplaneModePreference = (SwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    public static boolean isAvailable(Context context) {
        return context.getResources().getBoolean(R$bool.config_show_toggle_airplane) && !context.getPackageManager().hasSystemFeature("android.software.leanback");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return isAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_network;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        if (isAvailable()) {
            this.mAirplaneModeEnabler.start();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (isAvailable()) {
            this.mAirplaneModeEnabler.stop();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        this.mAirplaneModeEnabler.close();
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 1) {
            this.mAirplaneModeEnabler.setAirplaneModeInECM(i2 == -1, this.mAirplaneModePreference.isChecked());
        }
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return this.mAirplaneModeEnabler.isAirplaneModeOn();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService(TELECOM_SERVICE);
        if (telecomManager != null && telecomManager.isInEmergencyCall()) {
            Log.d(LOG_TAG, "setChecked, isInEmergencyCall.");
            Toast.makeText(this.mContext, R$string.airplane_cannot_use_during_call, 1).show();
            return false;
        } else if (isChecked() == z) {
            return false;
        } else {
            this.mAirplaneModeEnabler.setAirplaneMode(z);
            return true;
        }
    }

    @Override // com.android.settings.AirplaneModeEnabler.OnAirplaneModeChangedListener
    public void onAirplaneModeChanged(boolean z) {
        SwitchPreference switchPreference = this.mAirplaneModePreference;
        if (switchPreference != null) {
            switchPreference.setChecked(z);
        }
    }
}
