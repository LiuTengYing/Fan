package com.android.settings.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.SensorPrivacyManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.view.RotationPolicy;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.devicestate.DeviceStateRotationLockSettingsManager;
/* loaded from: classes.dex */
public class SmartAutoRotateController extends TogglePreferenceController implements LifecycleObserver {
    private final DeviceStateRotationLockSettingsManager mDeviceStateAutoRotateSettingsManager;
    private final DeviceStateRotationLockSettingsManager.DeviceStateRotationLockSettingsListener mDeviceStateRotationLockSettingsListener;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PowerManager mPowerManager;
    protected Preference mPreference;
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
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        updateState(this.mPreference);
    }

    public SmartAutoRotateController(Context context, String str) {
        super(context, str);
        this.mReceiver = new BroadcastReceiver() { // from class: com.android.settings.display.SmartAutoRotateController.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                SmartAutoRotateController smartAutoRotateController = SmartAutoRotateController.this;
                smartAutoRotateController.updateState(smartAutoRotateController.mPreference);
            }
        };
        this.mDeviceStateRotationLockSettingsListener = new DeviceStateRotationLockSettingsManager.DeviceStateRotationLockSettingsListener() { // from class: com.android.settings.display.SmartAutoRotateController$$ExternalSyntheticLambda0
            @Override // com.android.settingslib.devicestate.DeviceStateRotationLockSettingsManager.DeviceStateRotationLockSettingsListener
            public final void onSettingsChanged() {
                SmartAutoRotateController.this.lambda$new$0();
            }
        };
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        SensorPrivacyManager sensorPrivacyManager = SensorPrivacyManager.getInstance(context);
        this.mPrivacyManager = sensorPrivacyManager;
        sensorPrivacyManager.addSensorPrivacyListener(2, new SensorPrivacyManager.OnSensorPrivacyChangedListener() { // from class: com.android.settings.display.SmartAutoRotateController$$ExternalSyntheticLambda1
            public final void onSensorPrivacyChanged(int i, boolean z) {
                SmartAutoRotateController.this.lambda$new$1(i, z);
            }
        });
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mDeviceStateAutoRotateSettingsManager = DeviceStateRotationLockSettingsManager.getInstance(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(int i, boolean z) {
        updateState(this.mPreference);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (isRotationResolverServiceAvailable(this.mContext)) {
            return (isRotationLocked() || !hasSufficientPermission(this.mContext) || isCameraLocked() || isPowerSaveMode()) ? 5 : 0;
        }
        return 3;
    }

    protected boolean isRotationLocked() {
        if (DeviceStateAutoRotationHelper.isDeviceStateRotationEnabled(this.mContext)) {
            return this.mDeviceStateAutoRotateSettingsManager.isRotationLockedForAllStates();
        }
        return RotationPolicy.isRotationLocked(this.mContext);
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        if (preference != null) {
            preference.setEnabled(getAvailabilityStatus() == 0);
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

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mContext.registerReceiver(this.mReceiver, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
        if (this.mRotationPolicyListener == null) {
            this.mRotationPolicyListener = new RotationPolicy.RotationPolicyListener() { // from class: com.android.settings.display.SmartAutoRotateController.2
                public void onChange() {
                    SmartAutoRotateController smartAutoRotateController = SmartAutoRotateController.this;
                    smartAutoRotateController.updateState(smartAutoRotateController.mPreference);
                }
            };
        }
        RotationPolicy.registerRotationPolicyListener(this.mContext, this.mRotationPolicyListener);
        this.mDeviceStateAutoRotateSettingsManager.registerListener(this.mDeviceStateRotationLockSettingsListener);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mContext.unregisterReceiver(this.mReceiver);
        RotationPolicy.RotationPolicyListener rotationPolicyListener = this.mRotationPolicyListener;
        if (rotationPolicyListener != null) {
            RotationPolicy.unregisterRotationPolicyListener(this.mContext, rotationPolicyListener);
            this.mRotationPolicyListener = null;
        }
        this.mDeviceStateAutoRotateSettingsManager.unregisterListener(this.mDeviceStateRotationLockSettingsListener);
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return (isRotationLocked() || !hasSufficientPermission(this.mContext) || isCameraLocked() || isPowerSaveMode() || Settings.Secure.getInt(this.mContext.getContentResolver(), "camera_autorotate", 0) != 1) ? false : true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mMetricsFeatureProvider.action(this.mContext, 1751, z);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "camera_autorotate", z ? 1 : 0);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_display;
    }

    public static boolean isRotationResolverServiceAvailable(Context context) {
        ResolveInfo resolveService;
        PackageManager packageManager = context.getPackageManager();
        String rotationResolverPackageName = packageManager.getRotationResolverPackageName();
        return (TextUtils.isEmpty(rotationResolverPackageName) || (resolveService = packageManager.resolveService(new Intent("android.service.rotationresolver.RotationResolverService").setPackage(rotationResolverPackageName), 1048576)) == null || resolveService.serviceInfo == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasSufficientPermission(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String rotationResolverPackageName = packageManager.getRotationResolverPackageName();
        return rotationResolverPackageName != null && packageManager.checkPermission("android.permission.CAMERA", rotationResolverPackageName) == 0;
    }
}
