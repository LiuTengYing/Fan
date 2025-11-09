package com.android.settings.location;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settingslib.widget.RadioButtonPreference;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes.dex */
public class LocationAssistGnssController extends LocationBasePreferenceController implements RadioButtonPreference.OnClickListener {
    private static final int AGPS_MODE_REGISTED_NETWORK = 0;
    private static final String KEY_ENABLE_APGS_REGISTERED = "enable_agps_registered";
    private static final String KEY_LOCATION_ASSIST_GNSS = "location_assist_gnss";
    private static final String SETTINGS_SECURE_KEY_ASSISTED_GPS_ENABLE_OPTION = "assisted_gps_enable_option";
    private static final String TAG = "LocationAssistGnssController";
    private final Map<String, Integer> mAgpsModeKeyToValueMap;
    private final ContentResolver mContentResolver;
    private final Map<String, RadioButtonPreference> mKeyToPreferenceMap;
    private OnChangeListener mOnChangeListener;
    private RadioButtonPreference mPreference;
    private final String mPreferenceKey;
    private final Resources mResources;

    /* loaded from: classes.dex */
    public interface OnChangeListener {
        void onCheckedChanged(Preference preference);
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.location.LocationEnabler.LocationModeChangeListener
    public void onLocationModeChanged(int i, boolean z) {
    }

    @Override // com.android.settings.location.LocationBasePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public LocationAssistGnssController(Context context, String str) {
        super(context, str);
        this.mAgpsModeKeyToValueMap = new HashMap();
        this.mKeyToPreferenceMap = new HashMap();
        this.mContentResolver = context.getContentResolver();
        this.mResources = context.getResources();
        this.mPreferenceKey = str;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RadioButtonPreference radioButtonPreference = (RadioButtonPreference) preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = radioButtonPreference;
        radioButtonPreference.setOnClickListener(this);
    }

    protected void updatePreferenceCheckedState(int i) {
        if (getAgpsModeValue() == i) {
            this.mPreference.setChecked(true);
        }
    }

    private int getAgpsModeValue() {
        return Settings.Secure.getInt(this.mContentResolver, SETTINGS_SECURE_KEY_ASSISTED_GPS_ENABLE_OPTION, 0);
    }

    private void updatePreferenceMap(Preference preference) {
        if (this.mKeyToPreferenceMap.size() < 3) {
            this.mKeyToPreferenceMap.put(preference.getKey(), (RadioButtonPreference) preference);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        updatePreferenceMap((RadioButtonPreference) preference);
        this.mPreference.setChecked(false);
        updatePreferenceCheckedState(getAgpsValueToKeyMap().get(this.mPreference.getKey()).intValue());
    }

    @Override // com.android.settingslib.widget.RadioButtonPreference.OnClickListener
    public void onRadioButtonClicked(RadioButtonPreference radioButtonPreference) {
        handlePreferenceChange(getAgpsValueToKeyMap().get(this.mPreferenceKey).intValue());
        OnChangeListener onChangeListener = this.mOnChangeListener;
        if (onChangeListener != null) {
            onChangeListener.onCheckedChanged(this.mPreference);
        }
    }

    protected RadioButtonPreference getPreferenceFromKey(String str) {
        return this.mKeyToPreferenceMap.get(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void resetToDefault() {
        handlePreferenceChange(0);
        RadioButtonPreference preferenceFromKey = getPreferenceFromKey(KEY_ENABLE_APGS_REGISTERED);
        this.mPreference = preferenceFromKey;
        OnChangeListener onChangeListener = this.mOnChangeListener;
        if (onChangeListener != null) {
            onChangeListener.onCheckedChanged(preferenceFromKey);
        }
    }

    private Map<String, Integer> getAgpsValueToKeyMap() {
        if (this.mAgpsModeKeyToValueMap.size() == 0) {
            String[] stringArray = this.mResources.getStringArray(R$array.location_agps_mode_keys);
            int[] intArray = this.mResources.getIntArray(R$array.location_agps_mode_values);
            int length = intArray.length;
            for (int i = 0; i < length; i++) {
                this.mAgpsModeKeyToValueMap.put(stringArray[i], Integer.valueOf(intArray[i]));
            }
        }
        return this.mAgpsModeKeyToValueMap;
    }

    private void handlePreferenceChange(int i) {
        Settings.Secure.putInt(this.mContentResolver, SETTINGS_SECURE_KEY_ASSISTED_GPS_ENABLE_OPTION, i);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.mOnChangeListener = onChangeListener;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return this.mPreferenceKey;
    }
}
