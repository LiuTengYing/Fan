package com.android.settings.display;

import android.content.Context;
import android.content.IntentFilter;
import android.hardware.SensorPrivacyManager;
import android.view.View;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.widget.BannerMessagePreference;
/* loaded from: classes.dex */
public class SmartAutoRotateCameraStateController extends BasePreferenceController {
    private Preference mPreference;
    private final SensorPrivacyManager mPrivacyManager;

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

    public SmartAutoRotateCameraStateController(Context context, String str) {
        super(context, str);
        SensorPrivacyManager sensorPrivacyManager = SensorPrivacyManager.getInstance(context);
        this.mPrivacyManager = sensorPrivacyManager;
        sensorPrivacyManager.addSensorPrivacyListener(2, new SensorPrivacyManager.OnSensorPrivacyChangedListener() { // from class: com.android.settings.display.SmartAutoRotateCameraStateController$$ExternalSyntheticLambda1
            public final void onSensorPrivacyChanged(int i, boolean z) {
                SmartAutoRotateCameraStateController.this.lambda$new$0(i, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, boolean z) {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setVisible(isAvailable());
        }
        updateState(this.mPreference);
    }

    @VisibleForTesting
    boolean isCameraLocked() {
        return this.mPrivacyManager.isSensorPrivacyEnabled(2);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference(getPreferenceKey());
        this.mPreference = findPreference;
        ((BannerMessagePreference) findPreference).setPositiveButtonText(R$string.allow).setPositiveButtonOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.SmartAutoRotateCameraStateController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SmartAutoRotateCameraStateController.this.lambda$displayPreference$1(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$1(View view) {
        this.mPrivacyManager.setSensorPrivacy(3, 2, false);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (SmartAutoRotateController.isRotationResolverServiceAvailable(this.mContext) && isCameraLocked()) ? 1 : 3;
    }
}
