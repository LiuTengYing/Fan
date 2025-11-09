package com.android.settings.users;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.core.BasePreferenceController;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class TimeoutToUserZeroPreferenceController extends BasePreferenceController {
    private final String[] mEntries;
    private final String[] mValues;

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

    public TimeoutToUserZeroPreferenceController(Context context, String str) {
        super(context, str);
        this.mEntries = this.mContext.getResources().getStringArray(R$array.switch_to_user_zero_when_docked_timeout_entries);
        this.mValues = this.mContext.getResources().getStringArray(R$array.switch_to_user_zero_when_docked_timeout_values);
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        updateState(preferenceScreen.findPreference(getPreferenceKey()));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mContext.getResources().getBoolean(17891649)) {
            if (Settings.Global.getInt(this.mContext.getContentResolver(), "user_switcher_enabled", 0) != 1) {
                return 2;
            }
            return UserHandle.myUserId() == 0 ? 4 : 0;
        }
        return 3;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "timeout_to_user_zero", UserHandle.myUserId());
        List asList = Arrays.asList(this.mValues);
        if (stringForUser == null) {
            stringForUser = this.mValues[0];
        }
        return this.mEntries[asList.indexOf(stringForUser)];
    }
}
