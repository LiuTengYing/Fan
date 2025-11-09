package com.android.settings.security;

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
/* loaded from: classes.dex */
public class ChangeScreenLockPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, GearPreference.OnGearClickListener {
    protected final SettingsPreferenceFragment mHost;
    protected final LockPatternUtils mLockPatternUtils;
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    protected RestrictedPreference mPreference;
    protected final int mProfileChallengeUserId;
    private final ScreenLockPreferenceDetailsUtils mScreenLockPreferenceDetailUtils;
    protected final UserManager mUm;
    protected final int mUserId;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "unlock_set_or_change";
    }

    public ChangeScreenLockPreferenceController(Context context, SettingsPreferenceFragment settingsPreferenceFragment) {
        super(context);
        int myUserId = UserHandle.myUserId();
        this.mUserId = myUserId;
        UserManager userManager = (UserManager) context.getSystemService("user");
        this.mUm = userManager;
        this.mLockPatternUtils = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getLockPatternUtils(context);
        this.mHost = settingsPreferenceFragment;
        this.mProfileChallengeUserId = Utils.getManagedProfileId(userManager, myUserId);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mScreenLockPreferenceDetailUtils = new ScreenLockPreferenceDetailsUtils(context);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mScreenLockPreferenceDetailUtils.isAvailable();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        RestrictedPreference restrictedPreference = this.mPreference;
        if (restrictedPreference != null && (restrictedPreference instanceof GearPreference)) {
            if (this.mScreenLockPreferenceDetailUtils.shouldShowGearMenu()) {
                ((GearPreference) this.mPreference).setOnGearClickListener(this);
            } else {
                ((GearPreference) this.mPreference).setOnGearClickListener(null);
            }
        }
        updateSummary(preference, this.mUserId);
        disableIfPasswordQualityManaged(this.mUserId);
        if (this.mLockPatternUtils.isSeparateProfileChallengeEnabled(this.mProfileChallengeUserId)) {
            return;
        }
        disableIfPasswordQualityManaged(this.mProfileChallengeUserId);
    }

    @Override // com.android.settings.widget.GearPreference.OnGearClickListener
    public void onGearClick(GearPreference gearPreference) {
        if (TextUtils.equals(gearPreference.getKey(), getPreferenceKey())) {
            this.mMetricsFeatureProvider.logClickedPreference(gearPreference, gearPreference.getExtras().getInt(DashboardFragment.CATEGORY));
            this.mScreenLockPreferenceDetailUtils.openScreenLockSettings(this.mHost.getMetricsCategory());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (!TextUtils.equals(preference.getKey(), getPreferenceKey())) {
            return super.handlePreferenceTreeClick(preference);
        }
        return this.mScreenLockPreferenceDetailUtils.openChooseLockGenericFragment(this.mHost.getMetricsCategory());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void updateSummary(Preference preference, int i) {
        preference.setSummary(this.mScreenLockPreferenceDetailUtils.getSummary(i));
        this.mPreference.setEnabled(true);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void disableIfPasswordQualityManaged(int i) {
        RestrictedLockUtils.EnforcedAdmin checkIfPasswordQualityIsSet = RestrictedLockUtilsInternal.checkIfPasswordQualityIsSet(this.mContext, i);
        if (this.mScreenLockPreferenceDetailUtils.isPasswordQualityManaged(i, checkIfPasswordQualityIsSet)) {
            this.mPreference.setDisabledByAdmin(checkIfPasswordQualityIsSet);
        }
    }
}
