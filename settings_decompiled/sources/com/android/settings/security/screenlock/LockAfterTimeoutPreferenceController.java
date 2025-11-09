package com.android.settings.security.screenlock;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.preference.Preference;
import com.android.internal.widget.LockPatternUtils;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.display.TimeoutListPreference;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.security.trustagent.TrustAgentManager;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class LockAfterTimeoutPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {
    private final DevicePolicyManager mDPM;
    private final LockPatternUtils mLockPatternUtils;
    private final TrustAgentManager mTrustAgentManager;
    private final int mUserId;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "lock_after_timeout";
    }

    public LockAfterTimeoutPreferenceController(Context context, int i, LockPatternUtils lockPatternUtils) {
        super(context);
        this.mUserId = i;
        this.mLockPatternUtils = lockPatternUtils;
        this.mDPM = (DevicePolicyManager) context.getSystemService("device_policy");
        this.mTrustAgentManager = FeatureFactory.getFactory(context).getSecurityFeatureProvider().getTrustAgentManager();
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        if (this.mLockPatternUtils.isSecure(this.mUserId)) {
            int keyguardStoredPasswordQuality = this.mLockPatternUtils.getKeyguardStoredPasswordQuality(this.mUserId);
            return keyguardStoredPasswordQuality == 65536 || keyguardStoredPasswordQuality == 131072 || keyguardStoredPasswordQuality == 196608 || keyguardStoredPasswordQuality == 262144 || keyguardStoredPasswordQuality == 327680 || keyguardStoredPasswordQuality == 393216 || keyguardStoredPasswordQuality == 524288;
        }
        return false;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        TimeoutListPreference timeoutListPreference = (TimeoutListPreference) preference;
        setupLockAfterPreference(timeoutListPreference);
        updateLockAfterPreferenceSummary(timeoutListPreference);
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        try {
            Settings.Secure.putInt(this.mContext.getContentResolver(), "lock_screen_lock_after_timeout", Integer.parseInt((String) obj));
            updateState(preference);
            return true;
        } catch (NumberFormatException e) {
            Log.e("PrefControllerMixin", "could not persist lockAfter timeout setting", e);
            return true;
        }
    }

    private void setupLockAfterPreference(TimeoutListPreference timeoutListPreference) {
        timeoutListPreference.setValue(String.valueOf(Settings.Secure.getLong(this.mContext.getContentResolver(), "lock_screen_lock_after_timeout", 5000L)));
        if (this.mDPM != null) {
            timeoutListPreference.removeUnusableTimeouts(Math.max(0L, this.mDPM.getMaximumTimeToLock(null, UserHandle.myUserId()) - Math.max(0, Settings.System.getInt(this.mContext.getContentResolver(), "screen_off_timeout", 0))), RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(this.mContext));
        }
    }

    private void updateLockAfterPreferenceSummary(TimeoutListPreference timeoutListPreference) {
        String string;
        if (timeoutListPreference.isDisabledByAdmin()) {
            string = this.mDPM.getResources().getString("Settings.DISABLED_BY_IT_ADMIN_TITLE", new Supplier() { // from class: com.android.settings.security.screenlock.LockAfterTimeoutPreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateLockAfterPreferenceSummary$0;
                    lambda$updateLockAfterPreferenceSummary$0 = LockAfterTimeoutPreferenceController.this.lambda$updateLockAfterPreferenceSummary$0();
                    return lambda$updateLockAfterPreferenceSummary$0;
                }
            });
        } else {
            long j = Settings.Secure.getLong(this.mContext.getContentResolver(), "lock_screen_lock_after_timeout", 5000L);
            CharSequence[] entries = timeoutListPreference.getEntries();
            CharSequence[] entryValues = timeoutListPreference.getEntryValues();
            int i = 0;
            for (int i2 = 0; i2 < entryValues.length; i2++) {
                if (j >= Long.valueOf(entryValues[i2].toString()).longValue()) {
                    i = i2;
                }
            }
            CharSequence activeTrustAgentLabel = this.mTrustAgentManager.getActiveTrustAgentLabel(this.mContext, this.mLockPatternUtils);
            if (!TextUtils.isEmpty(activeTrustAgentLabel)) {
                if (Long.valueOf(entryValues[i].toString()).longValue() == 0) {
                    string = this.mContext.getString(R$string.lock_immediately_summary_with_exception, activeTrustAgentLabel);
                } else {
                    string = this.mContext.getString(R$string.lock_after_timeout_summary_with_exception, entries[i], activeTrustAgentLabel);
                }
            } else {
                string = this.mContext.getString(R$string.lock_after_timeout_summary, entries[i]);
            }
        }
        timeoutListPreference.setSummary(string);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateLockAfterPreferenceSummary$0() {
        return this.mContext.getString(R$string.disabled_by_policy_title);
    }
}
