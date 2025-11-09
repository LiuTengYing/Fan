package com.android.settings.datetime;

import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationFeaturesUtils;
import android.location.LocationManager;
import android.provider.Settings;
import com.android.settings.core.InstrumentedPreferenceFragment;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.datetime.UnisocLocationToggleDisabledDialogFragment;
/* loaded from: classes.dex */
public class UnisocLocationTimeDetectionPreferenceController extends TogglePreferenceController implements UnisocLocationToggleDisabledDialogFragment.GpsUpdateTimeCancelCallback {
    private static final int DIALOG_AUTO_TIME_GPS_CONFIRM = 1;
    private static final int DIALOG_LOCATION_DISABLED_WARNNING = 2;
    private static boolean GNSS_DISABLED = false;
    private static boolean SUPPORT_GPS_TIME = false;
    private static final String TAG = "location_date_time_detection";
    private InstrumentedPreferenceFragment mFragment;
    private final LocationManager mLocationManager;
    private UnisocAutoTimePreferenceHost mUpdatePreference;

    /* loaded from: classes.dex */
    public interface UnisocAutoTimePreferenceHost {
        void updatePreference();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public UnisocLocationTimeDetectionPreferenceController(Context context) {
        super(context, TAG);
        SUPPORT_GPS_TIME = LocationFeaturesUtils.getInstance(context).isSupportGpsTime();
        GNSS_DISABLED = LocationFeaturesUtils.getInstance(context).isGnssDisabled();
        this.mLocationManager = (LocationManager) context.getSystemService(LocationManager.class);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setFragmentAndUpdatePreference(InstrumentedPreferenceFragment instrumentedPreferenceFragment, UnisocAutoTimePreferenceHost unisocAutoTimePreferenceHost) {
        this.mFragment = instrumentedPreferenceFragment;
        this.mUpdatePreference = unisocAutoTimePreferenceHost;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return Settings.Global.getInt(this.mContext.getContentResolver(), "auto_time_gps", 0) > 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        if (z) {
            if (!this.mLocationManager.isLocationEnabled()) {
                new UnisocLocationToggleDisabledDialogFragment(this.mContext, 2, this).show(this.mFragment.getFragmentManager(), TAG);
                return false;
            }
            new UnisocLocationToggleDisabledDialogFragment(this.mContext, 1, this).show(this.mFragment.getFragmentManager(), TAG);
        }
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_time", !z ? 1 : 0);
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_time_gps", z ? 1 : 0);
        this.mUpdatePreference.updatePreference();
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!SUPPORT_GPS_TIME || GNSS_DISABLED) ? 3 : 0;
    }

    @Override // com.android.settings.datetime.UnisocLocationToggleDisabledDialogFragment.GpsUpdateTimeCancelCallback
    public void gpsUpdateTimeCancel() {
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_time", 1);
        Settings.Global.putInt(this.mContext.getContentResolver(), "auto_time_gps", 0);
        this.mUpdatePreference.updatePreference();
    }
}
