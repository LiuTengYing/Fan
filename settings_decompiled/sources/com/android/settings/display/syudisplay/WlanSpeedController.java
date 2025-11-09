package com.android.settings.display.syudisplay;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.RestrictedSwitchPreference;
/* loaded from: classes.dex */
public class WlanSpeedController extends BasePreferenceController implements Preference.OnPreferenceChangeListener {
    public static String netview_status_action = "com.fyt.action.netviewstatuschange";
    private String KEY;
    private Context mContext;
    private RestrictedSwitchPreference mPreference;
    private String wlanToOnOrOff;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public WlanSpeedController(Context context, String str) {
        super(context, str);
        this.KEY = "wireless_control_switch";
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(this.KEY);
        this.mPreference = restrictedSwitchPreference;
        if (restrictedSwitchPreference != null) {
            updateWlanToSwitch();
            this.mPreference.setChecked(this.wlanToOnOrOff.equals("1"));
        }
        super.displayPreference(preferenceScreen);
    }

    private void writeWlanToSwitch(boolean z) {
        Log.d("fangli", "=wlanToOnOrOff==" + this.wlanToOnOrOff);
        SystemProperties.set("persist.sys.wlantoshow", z ? "1" : "0");
        updateWlanToSwitch();
        this.mContext.sendBroadcast(new Intent(netview_status_action));
    }

    private void updateWlanToSwitch() {
        this.wlanToOnOrOff = SystemProperties.get("persist.sys.wlantoshow", "0");
        StringBuilder sb = new StringBuilder();
        sb.append(" wlanToOnOrOff==nul ?  ");
        sb.append(String.valueOf(this.wlanToOnOrOff == null));
        sb.append("  wlanshowPerfer==null ?  ");
        Log.d("fangli", sb.toString());
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        setChecked(booleanValue);
        writeWlanToSwitch(booleanValue);
        return false;
    }

    public void setChecked(boolean z) {
        this.mPreference.setChecked(z);
    }
}
