package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.SearchIndexableData;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.devicestate.DeviceStateRotationLockSettingsManager;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.List;
/* loaded from: classes.dex */
public class DeviceStateAutoRotateSettingController extends TogglePreferenceController implements LifecycleObserver {
    private final DeviceStateRotationLockSettingsManager mAutoRotateSettingsManager;
    private final int mDeviceState;
    private final String mDeviceStateDescription;
    private final DeviceStateRotationLockSettingsManager.DeviceStateRotationLockSettingsListener mDeviceStateRotationLockSettingsListener;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final int mOrder;
    private SwitchPreference mPreference;

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
    public boolean isSliceable() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateState(this.mPreference);
    }

    DeviceStateAutoRotateSettingController(Context context, int i, String str, int i2, MetricsFeatureProvider metricsFeatureProvider) {
        super(context, getPreferenceKeyForDeviceState(i));
        this.mDeviceStateRotationLockSettingsListener = new DeviceStateRotationLockSettingsManager.DeviceStateRotationLockSettingsListener() { // from class: com.android.settings.display.DeviceStateAutoRotateSettingController$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.devicestate.DeviceStateRotationLockSettingsManager.DeviceStateRotationLockSettingsListener
            public final void onSettingsChanged() {
                DeviceStateAutoRotateSettingController.this.lambda$new$0();
            }
        };
        this.mMetricsFeatureProvider = metricsFeatureProvider;
        this.mDeviceState = i;
        this.mDeviceStateDescription = str;
        this.mAutoRotateSettingsManager = DeviceStateRotationLockSettingsManager.getInstance(context);
        this.mOrder = i2;
    }

    public DeviceStateAutoRotateSettingController(Context context, int i, String str, int i2) {
        this(context, i, str, i2, FeatureFactory.getFactory(context).getMetricsFeatureProvider());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void init(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        this.mAutoRotateSettingsManager.registerListener(this.mDeviceStateRotationLockSettingsListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        this.mAutoRotateSettingsManager.unregisterListener(this.mDeviceStateRotationLockSettingsListener);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        SwitchPreference switchPreference = new SwitchPreference(this.mContext);
        this.mPreference = switchPreference;
        switchPreference.setTitle(this.mDeviceStateDescription);
        this.mPreference.setKey(getPreferenceKey());
        this.mPreference.setOrder(this.mOrder);
        preferenceScreen.addPreference(this.mPreference);
        super.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return DeviceStateAutoRotationHelper.isDeviceStateRotationEnabled(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return getPreferenceKeyForDeviceState(this.mDeviceState);
    }

    private static String getPreferenceKeyForDeviceState(int i) {
        return "auto_rotate_device_state_" + i;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return !this.mAutoRotateSettingsManager.isRotationLocked(this.mDeviceState);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        logSettingChanged(z);
        this.mAutoRotateSettingsManager.updateSetting(this.mDeviceState, !z);
        return true;
    }

    private void logSettingChanged(boolean z) {
        this.mMetricsFeatureProvider.action(this.mContext, 203, !z);
        this.mMetricsFeatureProvider.action(this.mContext, z ? 1790 : 1791, this.mDeviceState);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public void updateRawDataToIndex(List<SearchIndexableRaw> list) {
        SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(this.mContext);
        ((SearchIndexableData) searchIndexableRaw).key = getPreferenceKey();
        searchIndexableRaw.title = this.mDeviceStateDescription;
        searchIndexableRaw.screenTitle = this.mContext.getString(R$string.accelerometer_title);
        list.add(searchIndexableRaw);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }
}
