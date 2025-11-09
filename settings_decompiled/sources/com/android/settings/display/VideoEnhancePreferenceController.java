package com.android.settings.display;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemProperties;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$bool;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class VideoEnhancePreferenceController extends BasePreferenceController {
    private static final String KEY_VIDEO_ADVANCED_MODE = "video_enhance_mode";
    private final boolean isSupportVideoEnahance;
    private Context mContext;
    Preference mPreference;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_VIDEO_ADVANCED_MODE;
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

    public VideoEnhancePreferenceController(Context context, String str) {
        super(context, str);
        this.isSupportVideoEnahance = 1 == SystemProperties.getInt("persist.sys.pq.dci.enabled", 0);
        this.mContext = context;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        Activity activity = (Activity) this.mContext;
        if (activity != null && preference != null) {
            if (activity.isInMultiWindowMode()) {
                preference.setEnabled(false);
            } else {
                preference.setEnabled(true);
            }
        }
        final String name = VideoEnhanceActivity.class.getName();
        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.android.settings.display.VideoEnhancePreferenceController$$ExternalSyntheticLambda0
            @Override // androidx.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference2) {
                boolean lambda$updateState$0;
                lambda$updateState$0 = VideoEnhancePreferenceController.this.lambda$updateState$0(name, preference2);
                return lambda$updateState$0;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$updateState$0(String str, Preference preference) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", str);
        this.mContext.startActivity(intent);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mContext.getResources().getBoolean(R$bool.config_support_video_enhancer) && this.isSupportVideoEnahance) ? 0 : 3;
    }

    public void setPreferenceEnabled(boolean z) {
        Preference preference = this.mPreference;
        if (preference != null) {
            preference.setEnabled(z);
        }
    }
}
