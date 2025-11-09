package com.android.settings.display.darkmode;

import android.app.UiModeManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.AttributeSet;
import com.android.settings.R$string;
import com.android.settingslib.PrimarySwitchPreference;
import java.time.LocalTime;
/* loaded from: classes.dex */
public class DarkModePreference extends PrimarySwitchPreference {
    private Runnable mCallback;
    private DarkModeObserver mDarkModeObserver;
    private TimeFormatter mFormat;
    private PowerManager mPowerManager;
    private UiModeManager mUiModeManager;

    public DarkModePreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mDarkModeObserver = new DarkModeObserver(context);
        this.mUiModeManager = (UiModeManager) context.getSystemService(UiModeManager.class);
        this.mPowerManager = (PowerManager) context.getSystemService(PowerManager.class);
        this.mFormat = new TimeFormatter(context);
        Runnable runnable = new Runnable() { // from class: com.android.settings.display.darkmode.DarkModePreference$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                DarkModePreference.this.lambda$new$0();
            }
        };
        this.mCallback = runnable;
        this.mDarkModeObserver.subscribe(runnable);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        boolean isPowerSaveMode = this.mPowerManager.isPowerSaveMode();
        boolean z = (getContext().getResources().getConfiguration().uiMode & 32) != 0;
        setSwitchEnabled(!isPowerSaveMode);
        updateSummary(isPowerSaveMode, z);
    }

    @Override // androidx.preference.Preference
    public void onAttached() {
        super.onAttached();
        this.mDarkModeObserver.subscribe(this.mCallback);
    }

    @Override // androidx.preference.Preference
    public void onDetached() {
        super.onDetached();
        this.mDarkModeObserver.unsubscribe();
    }

    private void updateSummary(boolean z, boolean z2) {
        int i;
        String string;
        LocalTime customNightModeStart;
        int i2;
        int i3;
        int i4;
        int i5;
        if (z) {
            if (z2) {
                i5 = R$string.dark_ui_mode_disabled_summary_dark_theme_on;
            } else {
                i5 = R$string.dark_ui_mode_disabled_summary_dark_theme_off;
            }
            setSummary(getContext().getString(i5));
            return;
        }
        int nightMode = this.mUiModeManager.getNightMode();
        if (nightMode == 0) {
            Context context = getContext();
            if (z2) {
                i4 = R$string.dark_ui_summary_on_auto_mode_auto;
            } else {
                i4 = R$string.dark_ui_summary_off_auto_mode_auto;
            }
            string = context.getString(i4);
        } else if (nightMode == 3) {
            if (this.mUiModeManager.getNightModeCustomType() == 1) {
                Context context2 = getContext();
                if (z2) {
                    i3 = R$string.dark_ui_summary_on_auto_mode_custom_bedtime;
                } else {
                    i3 = R$string.dark_ui_summary_off_auto_mode_custom_bedtime;
                }
                string = context2.getString(i3);
            } else {
                if (z2) {
                    customNightModeStart = this.mUiModeManager.getCustomNightModeEnd();
                } else {
                    customNightModeStart = this.mUiModeManager.getCustomNightModeStart();
                }
                String of = this.mFormat.of(customNightModeStart);
                Context context3 = getContext();
                if (z2) {
                    i2 = R$string.dark_ui_summary_on_auto_mode_custom;
                } else {
                    i2 = R$string.dark_ui_summary_off_auto_mode_custom;
                }
                string = context3.getString(i2, of);
            }
        } else {
            Context context4 = getContext();
            if (z2) {
                i = R$string.dark_ui_summary_on_auto_mode_never;
            } else {
                i = R$string.dark_ui_summary_off_auto_mode_never;
            }
            string = context4.getString(i);
        }
        setSummary(string);
    }
}
