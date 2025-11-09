package com.android.settings.display;

import android.content.Context;
import android.os.PowerManager;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settingslib.widget.BannerMessagePreference;
/* loaded from: classes.dex */
public class AdaptiveSleepBatterySaverPreferenceController {
    private final Context mContext;
    private final PowerManager mPowerManager;
    BannerMessagePreference mPreference;

    public AdaptiveSleepBatterySaverPreferenceController(Context context) {
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mContext = context;
    }

    public void addToScreen(PreferenceScreen preferenceScreen) {
        initializePreference();
        preferenceScreen.addPreference(this.mPreference);
        updateVisibility();
    }

    boolean isPowerSaveMode() {
        return this.mPowerManager.isPowerSaveMode();
    }

    public void updateVisibility() {
        initializePreference();
        this.mPreference.setVisible(isPowerSaveMode());
    }

    private void initializePreference() {
        if (this.mPreference == null) {
            BannerMessagePreference bannerMessagePreference = new BannerMessagePreference(this.mContext);
            this.mPreference = bannerMessagePreference;
            bannerMessagePreference.setTitle(R$string.ambient_camera_summary_battery_saver_on);
            this.mPreference.setPositiveButtonText(R$string.disable_text);
            this.mPreference.setPositiveButtonOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.AdaptiveSleepBatterySaverPreferenceController$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AdaptiveSleepBatterySaverPreferenceController.this.lambda$initializePreference$0(view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initializePreference$0(View view) {
        this.mPowerManager.setPowerSaveModeEnabled(false);
    }
}
