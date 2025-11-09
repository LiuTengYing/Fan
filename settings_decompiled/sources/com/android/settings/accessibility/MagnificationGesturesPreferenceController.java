package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R$raw;
import com.android.settings.R$string;
import com.android.settings.core.TogglePreferenceController;
/* loaded from: classes.dex */
public class MagnificationGesturesPreferenceController extends TogglePreferenceController {
    private boolean mIsFromSUW;

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return 0;
    }

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
    public boolean isPublicSlice() {
        return true;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public MagnificationGesturesPreferenceController(Context context, String str) {
        super(context, str);
        this.mIsFromSUW = false;
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean isChecked() {
        return MagnificationPreferenceFragment.isChecked(this.mContext.getContentResolver(), "accessibility_display_magnification_enabled");
    }

    @Override // com.android.settings.core.TogglePreferenceController
    public boolean setChecked(boolean z) {
        return MagnificationPreferenceFragment.setChecked(this.mContext.getContentResolver(), "accessibility_display_magnification_enabled", z);
    }

    public void setIsFromSUW(boolean z) {
        this.mIsFromSUW = z;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            Bundle extras = preference.getExtras();
            populateMagnificationGesturesPreferenceExtras(extras, this.mContext);
            extras.putBoolean("checked", isChecked());
            extras.putBoolean("from_suw", this.mIsFromSUW);
            return true;
        }
        return false;
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public boolean isSliceable() {
        return TextUtils.equals(getPreferenceKey(), "screen_magnification_gestures_preference_screen");
    }

    @Override // com.android.settings.core.TogglePreferenceController, com.android.settings.slices.Sliceable
    public int getSliceHighlightMenuRes() {
        return R$string.menu_key_accessibility;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        int i;
        if (this.mIsFromSUW) {
            i = R$string.accessibility_screen_magnification_short_summary;
        } else if (isChecked()) {
            i = R$string.accessibility_feature_state_on;
        } else {
            i = R$string.accessibility_feature_state_off;
        }
        return this.mContext.getString(i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void populateMagnificationGesturesPreferenceExtras(Bundle bundle, Context context) {
        bundle.putString("preference_key", "accessibility_display_magnification_enabled");
        bundle.putInt("title_res", R$string.accessibility_screen_magnification_gestures_title);
        bundle.putCharSequence("intro", context.getString(R$string.accessibility_screen_magnification_intro_text));
        bundle.putCharSequence("html_description", MessageFormat.format(context.getString(R$string.accessibility_screen_magnification_summary), 1, 2, 3, 4, 5));
        bundle.putInt("video_resource", R$raw.accessibility_screen_magnification);
    }
}
