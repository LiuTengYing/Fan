package com.android.settings.display;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.NightDisplayListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import java.time.LocalTime;
/* loaded from: classes.dex */
public class NightDisplaySettings extends DashboardFragment implements NightDisplayListener.Callback {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.night_display_settings) { // from class: com.android.settings.display.NightDisplaySettings.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return ColorDisplayManager.isNightDisplayAvailable(context);
        }
    };
    private ColorDisplayManager mColorDisplayManager;
    private NightDisplayListener mNightDisplayListener;

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public int getDialogMetricsCategory(int i) {
        if (i != 0) {
            return i != 1 ? 0 : 589;
        }
        return 588;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "NightDisplaySettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 488;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Context context = getContext();
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mNightDisplayListener = new NightDisplayListener(context);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        this.mNightDisplayListener.setCallback(this);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        this.mNightDisplayListener.setCallback((NightDisplayListener.Callback) null);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.preference.PreferenceManager.OnPreferenceTreeClickListener
    public boolean onPreferenceTreeClick(Preference preference) {
        if ("night_display_end_time".equals(preference.getKey())) {
            writePreferenceClickMetric(preference);
            showDialog(1);
            return true;
        } else if ("night_display_start_time".equals(preference.getKey())) {
            writePreferenceClickMetric(preference);
            showDialog(0);
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCreateDialog$0(int i, TimePicker timePicker, int i2, int i3) {
        LocalTime of = LocalTime.of(i2, i3);
        if (i == 0) {
            this.mColorDisplayManager.setNightDisplayCustomStartTime(of);
        } else {
            this.mColorDisplayManager.setNightDisplayCustomEndTime(of);
        }
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settings.DialogCreatable
    public Dialog onCreateDialog(final int i) {
        LocalTime nightDisplayCustomEndTime;
        if (i == 0 || i == 1) {
            if (i == 0) {
                nightDisplayCustomEndTime = this.mColorDisplayManager.getNightDisplayCustomStartTime();
            } else {
                nightDisplayCustomEndTime = this.mColorDisplayManager.getNightDisplayCustomEndTime();
            }
            Context context = getContext();
            return new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() { // from class: com.android.settings.display.NightDisplaySettings$$ExternalSyntheticLambda0
                @Override // android.app.TimePickerDialog.OnTimeSetListener
                public final void onTimeSet(TimePicker timePicker, int i2, int i3) {
                    NightDisplaySettings.this.lambda$onCreateDialog$0(i, timePicker, i2, i3);
                }
            }, nightDisplayCustomEndTime.getHour(), nightDisplayCustomEndTime.getMinute(), DateFormat.is24HourFormat(context));
        }
        return super.onCreateDialog(i);
    }

    public void onActivated(boolean z) {
        updatePreferenceStates();
    }

    public void onAutoModeChanged(int i) {
        updatePreferenceStates();
    }

    public void onColorTemperatureChanged(int i) {
        updatePreferenceStates();
    }

    public void onCustomStartTimeChanged(LocalTime localTime) {
        updatePreferenceStates();
    }

    public void onCustomEndTimeChanged(LocalTime localTime) {
        updatePreferenceStates();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.night_display_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_night_display;
    }
}
