package com.android.settings.display;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import java.util.function.Supplier;
/* loaded from: classes.dex */
public class ScreenTimeoutPreferenceController extends BasePreferenceController {
    public static String PREF_NAME = "screen_timeout";
    private final MetricsFeatureProvider mMetricsFeatureProvider;
    private final CharSequence[] mTimeoutEntries;
    private final CharSequence[] mTimeoutValues;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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

    public ScreenTimeoutPreferenceController(Context context, String str) {
        super(context, str);
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
        this.mTimeoutEntries = context.getResources().getStringArray(R$array.screen_timeout_entries);
        this.mTimeoutValues = context.getResources().getStringArray(R$array.screen_timeout_values);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        long longValue = getMaxScreenTimeout().longValue();
        RestrictedLockUtils.EnforcedAdmin preferenceDisablingAdmin = getPreferenceDisablingAdmin(longValue);
        if (preferenceDisablingAdmin != null) {
            preference.setEnabled(false);
            preference.setSummary(((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)).getResources().getString("Settings.DISABLED_BY_IT_ADMIN_TITLE", new Supplier() { // from class: com.android.settings.display.ScreenTimeoutPreferenceController$$ExternalSyntheticLambda0
                @Override // java.util.function.Supplier
                public final Object get() {
                    String lambda$updateState$0;
                    lambda$updateState$0 = ScreenTimeoutPreferenceController.this.lambda$updateState$0();
                    return lambda$updateState$0;
                }
            }));
            ((RestrictedPreference) preference).setDisabledByAdmin(preferenceDisablingAdmin);
            return;
        }
        preference.setSummary(getTimeoutSummary(longValue));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String lambda$updateState$0() {
        return this.mContext.getString(R$string.disabled_by_policy_title);
    }

    private CharSequence getTimeoutSummary(long j) {
        CharSequence timeoutDescription = getTimeoutDescription(getCurrentScreenTimeout(), j);
        if (timeoutDescription == null) {
            CharSequence currentTimeout = getCurrentTimeout(30000L);
            this.mMetricsFeatureProvider.action(this.mContext, 1754, 30000);
            Settings.System.putLong(this.mContext.getContentResolver(), "screen_off_timeout", 30000L);
            timeoutDescription = currentTimeout;
        }
        return this.mContext.getString(R$string.screen_timeout_summary, timeoutDescription);
    }

    private Long getMaxScreenTimeout() {
        DevicePolicyManager devicePolicyManager;
        if (RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(this.mContext) != null && (devicePolicyManager = (DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)) != null) {
            return Long.valueOf(devicePolicyManager.getMaximumTimeToLock(null, UserHandle.myUserId()));
        }
        return Long.MAX_VALUE;
    }

    private RestrictedLockUtils.EnforcedAdmin getPreferenceDisablingAdmin(long j) {
        if (((DevicePolicyManager) this.mContext.getSystemService(DevicePolicyManager.class)) != null) {
            RestrictedLockUtils.EnforcedAdmin checkIfRestrictionEnforced = RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "no_config_screen_timeout", UserHandle.myUserId());
            return (checkIfRestrictionEnforced == null && getLargestTimeout(j) == null) ? RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(this.mContext) : checkIfRestrictionEnforced;
        }
        return null;
    }

    private long getCurrentScreenTimeout() {
        return Settings.System.getLong(this.mContext.getContentResolver(), "screen_off_timeout", 30000L);
    }

    private CharSequence getTimeoutDescription(long j, long j2) {
        CharSequence[] charSequenceArr;
        CharSequence[] charSequenceArr2;
        if (j < 0 || (charSequenceArr = this.mTimeoutEntries) == null || (charSequenceArr2 = this.mTimeoutValues) == null || charSequenceArr2.length != charSequenceArr.length) {
            return null;
        }
        if (j > j2) {
            return getLargestTimeout(j2);
        }
        return getCurrentTimeout(j);
    }

    private CharSequence getCurrentTimeout(long j) {
        int i = 0;
        while (true) {
            CharSequence[] charSequenceArr = this.mTimeoutValues;
            if (i >= charSequenceArr.length) {
                return null;
            }
            if (j == Long.parseLong(charSequenceArr[i].toString())) {
                return this.mTimeoutEntries[i];
            }
            i++;
        }
    }

    private CharSequence getLargestTimeout(long j) {
        CharSequence charSequence = null;
        int i = 0;
        while (true) {
            CharSequence[] charSequenceArr = this.mTimeoutValues;
            if (i >= charSequenceArr.length) {
                return charSequence;
            }
            if (Long.parseLong(charSequenceArr[i].toString()) <= j) {
                charSequence = this.mTimeoutEntries[i];
            }
            i++;
        }
    }
}
