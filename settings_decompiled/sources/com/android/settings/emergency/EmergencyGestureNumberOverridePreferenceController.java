package com.android.settings.emergency;

import android.content.Context;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.emergencynumber.EmergencyNumberUtils;
/* loaded from: classes.dex */
public class EmergencyGestureNumberOverridePreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    EmergencyNumberUtils mEmergencyNumberUtils;
    private final Handler mHandler;
    private Preference mPreference;
    private final ContentObserver mSettingsObserver;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
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

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public EmergencyGestureNumberOverridePreferenceController(Context context, String str) {
        super(context, str);
        this.mEmergencyNumberUtils = new EmergencyNumberUtils(context);
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mSettingsObserver = new EmergencyGestureNumberOverrideSettingsObserver(handler);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mContext.getResources().getBoolean(R$bool.config_show_emergency_gesture_settings) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String policeNumber = this.mEmergencyNumberUtils.getPoliceNumber();
        String string = this.mContext.getString(R$string.emergency_gesture_call_for_help_summary, policeNumber);
        int indexOf = string.indexOf(policeNumber);
        if (indexOf < 0) {
            return string;
        }
        Spannable newSpannable = Spannable.Factory.getInstance().newSpannable(string);
        PhoneNumberUtils.addTtsSpan(newSpannable, indexOf, policeNumber.length() + indexOf);
        return newSpannable;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.getContentResolver().registerContentObserver(EmergencyNumberUtils.EMERGENCY_NUMBER_OVERRIDE_AUTHORITY, false, this.mSettingsObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
    }

    /* loaded from: classes.dex */
    private class EmergencyGestureNumberOverrideSettingsObserver extends ContentObserver {
        EmergencyGestureNumberOverrideSettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (EmergencyGestureNumberOverridePreferenceController.this.mPreference != null) {
                EmergencyGestureNumberOverridePreferenceController emergencyGestureNumberOverridePreferenceController = EmergencyGestureNumberOverridePreferenceController.this;
                emergencyGestureNumberOverridePreferenceController.updateState(emergencyGestureNumberOverridePreferenceController.mPreference);
            }
        }
    }
}
