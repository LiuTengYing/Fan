package com.android.settings.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorPrivacyManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.view.RotationPolicy;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class SmartAutoRotatePreferenceController extends TogglePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PowerManager mPowerManager;
    private Preference mPreference;
    private final SensorPrivacyManager mPrivacyManager;
    private final BroadcastReceiver mReceiver;
    private RotationPolicy.RotationPolicyListener mRotationPolicyListener;

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

    public SmartAutoRotatePreferenceController(Context context, String str) {
        super(context, str);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.display.SmartAutoRotatePreferenceController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                SmartAutoRotatePreferenceController smartAutoRotatePreferenceController = SmartAutoRotatePreferenceController.this;
                smartAutoRotatePreferenceController.refreshSummary(smartAutoRotatePreferenceController.mPreference);
            }
        };
        SensorPrivacyManager sensorPrivacyManager = SensorPrivacyManager.getInstance(context);
        this.mPrivacyManager = sensorPrivacyManager;
        sensorPrivacyManager.addSensorPrivacyListener(2, new SensorPrivacyManager.OnSensorPrivacyChangedListener() { // from class: com.android.settings.display.SmartAutoRotatePreferenceController$$ExternalSyntheticLambda0
            public final void onSensorPrivacyChanged(int i, boolean z) {
                SmartAutoRotatePreferenceController.this.lambda$new$0(i, z);
            }
        });
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, boolean z) {
        refreshSummary(this.mPreference);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (!RotationPolicy.isRotationLockToggleVisible(this.mContext) || DeviceStateAutoRotationHelper.isDeviceStateRotationEnabled(this.mContext)) ? 3 : 0;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "auto_rotate");
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        refreshSummary(this.mPreference);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
        if (this.mRotationPolicyListener == null) {
            this.mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() { // from class: com.android.settings.display.SmartAutoRotatePreferenceController.2
                public void onChange() {
                    if (SmartAutoRotatePreferenceController.this.mPreference != null) {
                        SmartAutoRotatePreferenceController smartAutoRotatePreferenceController = SmartAutoRotatePreferenceController.this;
                        smartAutoRotatePreferenceController.updateState(smartAutoRotatePreferenceController.mPreference);
                    }
                }
            };
        }
        RotationPolicy.registerRotationPolicyListener(this.mContext, this.mRotationPolicyListener);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
        RotationPolicy.RotationPolicyListener rotationPolicyListener = this.mRotationPolicyListener;
        if (rotationPolicyListener != null) {
            RotationPolicy.unregisterRotationPolicyListener(this.mContext, rotationPolicyListener);
        }
    }

    @VisibleForTesting
    boolean isCameraLocked() {
        return this.mPrivacyManager.isSensorPrivacyEnabled(2);
    }

    @VisibleForTesting
    boolean isPowerSaveMode() {
        return this.mPowerManager.isPowerSaveMode();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return !RotationPolicy.isRotationLocked(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        boolean z2 = !z;
        this.mMetricsFeatureProvider.action(this.mContext, 203, z2);
        RotationPolicy.setRotationLock(this.mContext, z2);
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i = R$string.auto_rotate_option_off;
        if (!RotationPolicy.isRotationLocked(this.mContext)) {
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "camera_autorotate", 0, -2) == 1 && SmartAutoRotateController.isRotationResolverServiceAvailable(this.mContext) && SmartAutoRotateController.hasSufficientPermission(this.mContext) && !isCameraLocked() && !isPowerSaveMode()) {
                i = R$string.auto_rotate_option_face_based;
            } else {
                i = R$string.auto_rotate_option_on;
            }
        }
        return this.mContext.getString(i);
    }
}
