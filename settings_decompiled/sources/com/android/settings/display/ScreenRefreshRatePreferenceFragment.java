package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class ScreenRefreshRatePreferenceFragment extends RadioButtonPickerFragment {
    static String AUTO = null;
    static float DEFAULT_REFRESH_RATE = 60.0f;
    static String NINETY = null;
    static String ONE_HUNDRED_AND_TWENTY = null;
    static float PEAK_REFRESH_RATE = 120.0f;
    static float SECOND_PEAK_REFRESH_RATE = 90.0f;
    static String SIXTY;
    private ContentObserver mRefreshRateObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.display.ScreenRefreshRatePreferenceFragment.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            ScreenRefreshRatePreferenceFragment.this.updateCandidates();
        }
    };
    private String[] mRefreshRateSummaries;
    private String[] mRefreshRateTitles;
    private String[] mRefreshRateValues;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.screen_refresh_rate_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        if (isForcePeakRefreshRateEnabled(getContext())) {
            return NINETY;
        }
        if (isForceDefaultRefreshRateEnabled(getContext())) {
            return SIXTY;
        }
        if (isForceNewPeakRefreshRateEnabled(getContext())) {
            return ONE_HUNDRED_AND_TWENTY;
        }
        return AUTO;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        Log.d("ScreenRefreshRatePreferenceFragment", "set default key:" + str);
        setCurrentRefreshMode(str);
        return true;
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
        ContentResolver contentResolver = getContext().getContentResolver();
        Uri uriFor = Settings.System.getUriFor("min_refresh_rate");
        Uri uriFor2 = Settings.System.getUriFor("peak_refresh_rate");
        contentResolver.registerContentObserver(uriFor, false, this.mRefreshRateObserver);
        contentResolver.registerContentObserver(uriFor2, false, this.mRefreshRateObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onStop() {
        super.onStop();
        getContext().getContentResolver().unregisterContentObserver(this.mRefreshRateObserver);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mRefreshRateValues = context.getResources().getStringArray(R$array.refresh_rate_values);
        this.mRefreshRateTitles = context.getResources().getStringArray(R$array.refresh_rate_valuese_titles);
        this.mRefreshRateSummaries = context.getResources().getStringArray(R$array.refresh_rate_valuese_summaries);
        String[] strArr = this.mRefreshRateValues;
        AUTO = strArr[0];
        SIXTY = strArr[1];
        NINETY = strArr[2];
        ONE_HUNDRED_AND_TWENTY = strArr[3];
    }

    private void setCurrentRefreshMode(String str) {
        if (str.equals(AUTO) || str.equals(SIXTY)) {
            if (str.equals(AUTO)) {
                Settings.System.putFloat(getContext().getContentResolver(), "peak_refresh_rate", PEAK_REFRESH_RATE);
            } else {
                Settings.System.putFloat(getContext().getContentResolver(), "peak_refresh_rate", DEFAULT_REFRESH_RATE);
            }
            Settings.System.putFloat(getContext().getContentResolver(), "min_refresh_rate", 0.0f);
        } else if (str.equals(NINETY)) {
            Settings.System.putFloat(getContext().getContentResolver(), "min_refresh_rate", SECOND_PEAK_REFRESH_RATE);
            Settings.System.putFloat(getContext().getContentResolver(), "peak_refresh_rate", SECOND_PEAK_REFRESH_RATE);
        } else if (str.equals(ONE_HUNDRED_AND_TWENTY)) {
            Settings.System.putFloat(getContext().getContentResolver(), "min_refresh_rate", PEAK_REFRESH_RATE);
            Settings.System.putFloat(getContext().getContentResolver(), "peak_refresh_rate", PEAK_REFRESH_RATE);
        } else {
            Log.d("ScreenRefreshRatePreferenceFragment", "unknown key:" + str);
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (true) {
            String[] strArr = this.mRefreshRateValues;
            if (i >= strArr.length) {
                return arrayList;
            }
            arrayList.add(new RefreshRateCandidateInfo(strArr[i], this.mRefreshRateTitles[i], this.mRefreshRateSummaries[i]));
            i++;
        }
    }

    public static boolean isForceDefaultRefreshRateEnabled(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), "peak_refresh_rate", getDefaultPeakRefreshRate(context)) == DEFAULT_REFRESH_RATE;
    }

    public static boolean isForcePeakRefreshRateEnabled(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), "peak_refresh_rate", getDefaultPeakRefreshRate(context)) == SECOND_PEAK_REFRESH_RATE;
    }

    public static boolean isForceNewPeakRefreshRateEnabled(Context context) {
        return Settings.System.getFloat(context.getContentResolver(), "min_refresh_rate", 0.0f) >= PEAK_REFRESH_RATE;
    }

    public static float getDefaultPeakRefreshRate(Context context) {
        float f = DeviceConfig.getFloat("display_manager", "peak_refresh_rate_default", -1.0f);
        if (f == -1.0f) {
            f = context.getResources().getInteger(17694794);
        }
        Log.d("ScreenRefreshRatePreferenceFragment", "getDefaultPeakRefreshRate : " + f);
        return f;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected void addStaticPreferences(PreferenceScreen preferenceScreen) {
        Log.d("ScreenRefreshRatePreferenceFragment", "addStaticPreferences: ");
        FooterPreference footerPreference = new FooterPreference(getContext());
        footerPreference.setTitle(getContext().getString(R$string.refresh_rate_tip));
        preferenceScreen.addPreference(footerPreference);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(SelectorWithWidgetPreference selectorWithWidgetPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        if (candidateInfo instanceof RefreshRateCandidateInfo) {
            Log.d("ScreenRefreshRatePreferenceFragment", "bindPreferenceExtra: key = " + str);
            selectorWithWidgetPreference.setSummary(((RefreshRateCandidateInfo) candidateInfo).loadSummary());
            selectorWithWidgetPreference.setAppendixVisibility(8);
        }
    }

    /* loaded from: classes.dex */
    class RefreshRateCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final String mSummary;
        private final String mTitle;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        RefreshRateCandidateInfo(String str, String str2, String str3) {
            super(true);
            this.mKey = str;
            this.mTitle = str2;
            this.mSummary = str3;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mTitle;
        }

        public CharSequence loadSummary() {
            return this.mSummary;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
        }
    }
}
