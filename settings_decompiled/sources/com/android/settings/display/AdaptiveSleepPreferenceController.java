package com.android.settings.display;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.SensorPrivacyManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.bluetooth.RestrictionUtils;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class AdaptiveSleepPreferenceController {
    private final Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final PackageManager mPackageManager;
    private final PowerManager mPowerManager;
    RestrictedSwitchPreference mPreference;
    private final SensorPrivacyManager mPrivacyManager;
    private final RestrictionUtils mRestrictionUtils;

    public AdaptiveSleepPreferenceController(Context context, RestrictionUtils restrictionUtils) {
        this.mContext = context;
        this.mRestrictionUtils = restrictionUtils;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mPrivacyManager = SensorPrivacyManager.getInstance(context);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mPackageManager = context.getPackageManager();
    }

    public AdaptiveSleepPreferenceController(Context context) {
        this(context, new RestrictionUtils());
    }

    public void addToScreen(PreferenceScreen preferenceScreen) {
        updatePreference();
        preferenceScreen.addPreference(this.mPreference);
    }

    public void updatePreference() {
        initializePreference();
        RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = this.mRestrictionUtils.checkIfRestrictionEnforced(this.mContext, "no_config_screen_timeout");
        if (checkIfRestrictionEnforced != null) {
            this.mPreference.setDisabledByAdmin(checkIfRestrictionEnforced);
            return;
        }
        this.mPreference.setChecked(isChecked());
        this.mPreference.setEnabled((!hasSufficientPermission(this.mPackageManager) || isCameraLocked() || isPowerSaveMode()) ? false : true);
    }

    void initializePreference() {
        if (this.mPreference == null) {
            RestrictedSwitchPreference restrictedSwitchPreference = new RestrictedSwitchPreference(this.mContext);
            this.mPreference = restrictedSwitchPreference;
            restrictedSwitchPreference.setTitle(R$string.adaptive_sleep_title);
            this.mPreference.setSummary(R$string.adaptive_sleep_description);
            this.mPreference.setChecked(isChecked());
            this.mPreference.setKey("adaptive_sleep");
            this.mPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.display.AdaptiveSleepPreferenceController$$ExternalSyntheticLambda0
                @Override // androidx.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    boolean lambda$initializePreference$0;
                    lambda$initializePreference$0 = AdaptiveSleepPreferenceController.this.lambda$initializePreference$0(preference);
                    return lambda$initializePreference$0;
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initializePreference$0(Preference preference) {
        boolean isChecked = ((RestrictedSwitchPreference) preference).isChecked();
        this.mMetricsFeatureProvider.action(this.mContext, 1755, isChecked);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "adaptive_sleep", isChecked ? 1 : 0);
        return true;
    }

    boolean isChecked() {
        return (!hasSufficientPermission(this.mContext.getPackageManager()) || isCameraLocked() || isPowerSaveMode() || Settings.Secure.getInt(this.mContext.getContentResolver(), "adaptive_sleep", 0) == 0) ? false : true;
    }

    boolean isCameraLocked() {
        return this.mPrivacyManager.isSensorPrivacyEnabled(2);
    }

    boolean isPowerSaveMode() {
        return this.mPowerManager.isPowerSaveMode();
    }

    public static int isControllerAvailable(Context context) {
        return isAdaptiveSleepSupported(context) ? 1 : 3;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean isAdaptiveSleepSupported(Context context) {
        return context.getResources().getBoolean(17891343) && isAttentionServiceAvailable(context);
    }

    private static boolean isAttentionServiceAvailable(Context context) {
        ResolveInfo resolveService;
        PackageManager packageManager = context.getPackageManager();
        String attentionServicePackageName = packageManager.getAttentionServicePackageName();
        return (TextUtils.isEmpty(attentionServicePackageName) || (resolveService = packageManager.resolveService(new Intent("android.service.attention.AttentionService").setPackage(attentionServicePackageName), 1048576)) == null || resolveService.serviceInfo == null) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static boolean hasSufficientPermission(PackageManager packageManager) {
        String attentionServicePackageName = packageManager.getAttentionServicePackageName();
        return attentionServicePackageName != null && packageManager.checkPermission("android.permission.CAMERA", attentionServicePackageName) == 0;
    }
}
