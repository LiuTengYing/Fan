package com.android.settings.accessibility;

import android.content.Context;
import android.content.IntentFilter;
import android.view.View;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$id;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.instrumentation.SettingsStatsLog;
import com.android.settingslib.widget.LayoutPreference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class TextReadingResetController extends BasePreferenceController {
    private int mEntryPoint;
    private final View.OnClickListener mOnResetClickListener;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface ResetStateListener {
        void resetState();
    }

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

    /* JADX INFO: Access modifiers changed from: package-private */
    public TextReadingResetController(Context context, String str, View.OnClickListener onClickListener) {
        super(context, str);
        this.mOnResetClickListener = onClickListener;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        ((LayoutPreference) preferenceScreen.findPreference(getPreferenceKey())).findViewById(R$id.reset_button).setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.accessibility.TextReadingResetController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                TextReadingResetController.this.lambda$displayPreference$0(view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$displayPreference$0(View view) {
        View.OnClickListener onClickListener = this.mOnResetClickListener;
        if (onClickListener != null) {
            onClickListener.onClick(view);
            SettingsStatsLog.write(454, AccessibilityStatsLogUtils.convertToItemKeyName(getPreferenceKey()), -1, AccessibilityStatsLogUtils.convertToEntryPoint(this.mEntryPoint));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setEntryPoint(int i) {
        this.mEntryPoint = i;
    }
}
