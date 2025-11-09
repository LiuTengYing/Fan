package com.android.settings.gestures;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;
import androidx.preference.Preference;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.LabeledSeekBarPreference;
/* loaded from: classes.dex */
public class GestureNavigationSettingsFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.gesture_navigation_settings) { // from class: com.android.settings.gestures.GestureNavigationSettingsFragment.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return SystemNavigationPreferenceController.isGestureAvailable(context);
        }
    };
    private float[] mBackGestureInsetScales;
    private float mDefaultBackGestureInset;
    private BackGestureIndicatorView mIndicatorView;
    private WindowManager mWindowManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "GestureNavigationSettingsFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1748;
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIndicatorView = new BackGestureIndicatorView(getActivity());
        this.mWindowManager = (WindowManager) getActivity().getSystemService("window");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        Resources resources = getActivity().getResources();
        this.mDefaultBackGestureInset = resources.getDimensionPixelSize(17105064);
        this.mBackGestureInsetScales = getFloatArray(resources.obtainTypedArray(17235997));
        initSeekBarPreference("gesture_left_back_sensitivity");
        initSeekBarPreference("gesture_right_back_sensitivity");
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        WindowManager windowManager = this.mWindowManager;
        BackGestureIndicatorView backGestureIndicatorView = this.mIndicatorView;
        windowManager.addView(backGestureIndicatorView, backGestureIndicatorView.getLayoutParams(getActivity().getWindow().getAttributes()));
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        this.mWindowManager.removeView(this.mIndicatorView);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.gesture_navigation_settings;
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_uri_default;
    }

    private void initSeekBarPreference(final String str) {
        LabeledSeekBarPreference labeledSeekBarPreference = (LabeledSeekBarPreference) getPreferenceScreen().findPreference(str);
        labeledSeekBarPreference.setContinuousUpdates(true);
        labeledSeekBarPreference.setHapticFeedbackMode(1);
        final String str2 = str == "gesture_left_back_sensitivity" ? "back_gesture_inset_scale_left" : "back_gesture_inset_scale_right";
        float f = Settings.Secure.getFloat(getContext().getContentResolver(), str2, 1.0f);
        float f2 = Float.MAX_VALUE;
        int i = -1;
        int i2 = 0;
        while (true) {
            float[] fArr = this.mBackGestureInsetScales;
            if (i2 < fArr.length) {
                float abs = Math.abs(fArr[i2] - f);
                if (abs < f2) {
                    i = i2;
                    f2 = abs;
                }
                i2++;
            } else {
                labeledSeekBarPreference.setProgress(i);
                labeledSeekBarPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.gestures.GestureNavigationSettingsFragment$$ExternalSyntheticLambda0
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean lambda$initSeekBarPreference$0;
                        lambda$initSeekBarPreference$0 = GestureNavigationSettingsFragment.this.lambda$initSeekBarPreference$0(str, preference, obj);
                        return lambda$initSeekBarPreference$0;
                    }
                });
                labeledSeekBarPreference.setOnPreferenceChangeStopListener(new Preference.OnPreferenceChangeListener() { // from class: com.android.settings.gestures.GestureNavigationSettingsFragment$$ExternalSyntheticLambda1
                    @Override // androidx.preference.Preference.OnPreferenceChangeListener
                    public final boolean onPreferenceChange(Preference preference, Object obj) {
                        boolean lambda$initSeekBarPreference$1;
                        lambda$initSeekBarPreference$1 = GestureNavigationSettingsFragment.this.lambda$initSeekBarPreference$1(str, str2, preference, obj);
                        return lambda$initSeekBarPreference$1;
                    }
                });
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initSeekBarPreference$0(String str, Preference preference, Object obj) {
        this.mIndicatorView.setIndicatorWidth((int) (this.mDefaultBackGestureInset * this.mBackGestureInsetScales[((Integer) obj).intValue()]), str == "gesture_left_back_sensitivity");
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$initSeekBarPreference$1(String str, String str2, Preference preference, Object obj) {
        this.mIndicatorView.setIndicatorWidth(0, str == "gesture_left_back_sensitivity");
        Settings.Secure.putFloat(getContext().getContentResolver(), str2, this.mBackGestureInsetScales[((Integer) obj).intValue()]);
        return true;
    }

    private static float[] getFloatArray(TypedArray typedArray) {
        int length = typedArray.length();
        float[] fArr = new float[length];
        for (int i = 0; i < length; i++) {
            fArr[i] = typedArray.getFloat(i, 1.0f);
        }
        typedArray.recycle();
        return fArr;
    }
}
