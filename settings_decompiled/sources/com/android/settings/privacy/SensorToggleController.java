package com.android.settings.privacy;

import android.content.Context;
import android.content.IntentFilter;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
import com.android.settings.utils.SensorPrivacyManagerHelper;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import java.util.concurrent.Executor;
/* loaded from: classes.dex */
public abstract class SensorToggleController extends TogglePreferenceController {
    private final Executor mCallbackExecutor;
    protected final SensorPrivacyManagerHelper mSensorPrivacyManagerHelper;

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    protected String getRestriction() {
        return null;
    }

    public abstract int getSensor();

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SensorToggleController(Context context, String str) {
        super(context, str);
        this.mSensorPrivacyManagerHelper = SensorPrivacyManagerHelper.getInstance(context);
        this.mCallbackExecutor = context.getMainExecutor();
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return !this.mSensorPrivacyManagerHelper.isSensorBlocked(getSensor());
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        this.mSensorPrivacyManagerHelper.setSensorBlockedForProfileGroup(2, getSensor(), !z);
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(final PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        RestrictedSwitchPreference restrictedSwitchPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        if (restrictedSwitchPreference != null) {
            restrictedSwitchPreference.setDisabledByAdmin(RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, getRestriction(), this.mContext.getUserId()));
        }
        this.mSensorPrivacyManagerHelper.addSensorBlockedListener(getSensor(), new SensorPrivacyManagerHelper.Callback() { // from class: com.android.settings.privacy.SensorToggleController$$ExternalSyntheticLambda0
            @Override // com.android.settings.utils.SensorPrivacyManagerHelper.Callback
            public final void onSensorPrivacyChanged(int i, boolean z) {
                SensorToggleController.this.lambda$displayPreference$0(preferenceScreen, i, z);
            }
        }, this.mCallbackExecutor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(PreferenceScreen preferenceScreen, int i, boolean z) {
        updateState(preferenceScreen.findPreference(this.mPreferenceKey));
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_privacy;
    }
}
