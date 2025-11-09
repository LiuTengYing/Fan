package com.android.settings.display;

import android.content.Context;
import android.hardware.SensorPrivacyManager;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settings.R$string;
import com.android.settingslib.widget.BannerMessagePreference;
/* loaded from: classes.dex */
public class AdaptiveSleepCameraStatePreferenceController {
    private final Context mContext;
    @VisibleForTesting
    BannerMessagePreference mPreference;
    private final SensorPrivacyManager mPrivacyManager;

    public AdaptiveSleepCameraStatePreferenceController(Context context) {
        SensorPrivacyManager sensorPrivacyManager = SensorPrivacyManager.getInstance(context);
        this.mPrivacyManager = sensorPrivacyManager;
        sensorPrivacyManager.addSensorPrivacyListener(2, new SensorPrivacyManager.OnSensorPrivacyChangedListener() { // from class: com.android.settings.display.AdaptiveSleepCameraStatePreferenceController$$ExternalSyntheticLambda0
            public final void onSensorPrivacyChanged(int i, boolean z) {
                AdaptiveSleepCameraStatePreferenceController.this.lambda$new$0(i, z);
            }
        });
        this.mContext = context;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, boolean z) {
        updateVisibility();
    }

    public void addToScreen(PreferenceScreen preferenceScreen) {
        initializePreference();
        preferenceScreen.addPreference(this.mPreference);
        updateVisibility();
    }

    @VisibleForTesting
    boolean isCameraLocked() {
        return this.mPrivacyManager.isSensorPrivacyEnabled(2);
    }

    public void updateVisibility() {
        initializePreference();
        this.mPreference.setVisible(isCameraLocked());
    }

    private void initializePreference() {
        if (this.mPreference == null) {
            BannerMessagePreference bannerMessagePreference = new BannerMessagePreference(this.mContext);
            this.mPreference = bannerMessagePreference;
            bannerMessagePreference.setTitle(R$string.auto_rotate_camera_lock_title);
            this.mPreference.setSummary(R$string.adaptive_sleep_camera_lock_summary);
            this.mPreference.setPositiveButtonText(R$string.allow);
            this.mPreference.setPositiveButtonOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.AdaptiveSleepCameraStatePreferenceController$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdaptiveSleepCameraStatePreferenceController.this.lambda$initializePreference$1(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initializePreference$1(View view) {
        this.mPrivacyManager.setSensorPrivacy(3, 2, false);
    }
}
