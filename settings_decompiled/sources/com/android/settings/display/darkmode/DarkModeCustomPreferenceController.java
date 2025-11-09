package com.android.settings.display.darkmode;

import android.app.TimePickerDialog;
import android.app.UiModeManager;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.TimePicker;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
import java.time.LocalTime;
/* loaded from: classes.dex */
public class DarkModeCustomPreferenceController extends BasePreferenceController {
    private static final String END_TIME_KEY = "dark_theme_end_time";
    private static final String START_TIME_KEY = "dark_theme_start_time";
    private TimeFormatter mFormat;
    private DarkModeSettingsFragment mFragmet;
    private final UiModeManager mUiModeManager;

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

    public DarkModeCustomPreferenceController(Context context, String str) {
        super(context, str);
        this.mFormat = new TimeFormatter(this.mContext);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
    }

    public DarkModeCustomPreferenceController(Context context, String str, DarkModeSettingsFragment darkModeSettingsFragment) {
        this(context, str);
        this.mFragmet = darkModeSettingsFragment;
    }

    public DarkModeCustomPreferenceController(Context context, String str, DarkModeSettingsFragment darkModeSettingsFragment, TimeFormatter timeFormatter) {
        this(context, str, darkModeSettingsFragment);
        this.mFormat = timeFormatter;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mUiModeManager.getNightMode() == 3 && this.mUiModeManager.getNightModeCustomType() == 0) ? 0 : 2;
    }

    public TimePickerDialog getDialog() {
        LocalTime customNightModeEnd;
        if (TextUtils.equals(getPreferenceKey(), START_TIME_KEY)) {
            customNightModeEnd = this.mUiModeManager.getCustomNightModeStart();
        } else {
            customNightModeEnd = this.mUiModeManager.getCustomNightModeEnd();
        }
        return new TimePickerDialog(this.mContext, new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.display.darkmode.DarkModeCustomPreferenceController$$ExternalSyntheticLambda0
            @Override // android.app.TimePickerDialog.OnTimeSetListener
            public final void onTimeSet(TimePicker timePicker, int i, int i2) {
                DarkModeCustomPreferenceController.this.lambda$getDialog$0(timePicker, i, i2);
            }
        }, customNightModeEnd.getHour(), customNightModeEnd.getMinute(), this.mFormat.is24HourFormat());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getDialog$0(TimePicker timePicker, int i, int i2) {
        LocalTime of = LocalTime.of(i, i2);
        if (TextUtils.equals(getPreferenceKey(), START_TIME_KEY)) {
            this.mUiModeManager.setCustomNightModeStart(of);
        } else {
            this.mUiModeManager.setCustomNightModeEnd(of);
        }
        DarkModeSettingsFragment darkModeSettingsFragment = this.mFragmet;
        if (darkModeSettingsFragment != null) {
            darkModeSettingsFragment.refresh();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void refreshSummary(Preference preference) {
        LocalTime customNightModeEnd;
        if (TextUtils.equals(getPreferenceKey(), START_TIME_KEY)) {
            customNightModeEnd = this.mUiModeManager.getCustomNightModeStart();
        } else {
            customNightModeEnd = this.mUiModeManager.getCustomNightModeEnd();
        }
        preference.setSummary(this.mFormat.of(customNightModeEnd));
    }
}
