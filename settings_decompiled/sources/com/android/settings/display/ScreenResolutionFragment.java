package com.android.settings.display;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.text.TextUtils;
import android.view.Display;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$array;
import com.android.settings.R$drawable;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.FooterPreference;
import com.android.settingslib.widget.IllustrationPreference;
import com.android.settingslib.widget.SelectorWithWidgetPreference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class ScreenResolutionFragment extends RadioButtonPickerFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.screen_resolution_settings) { // from class: com.android.settings.display.ScreenResolutionFragment.1
        boolean mIsFHDSupport = false;
        boolean mIsQHDSupport = false;

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return new ScreenResolutionController(context, "fragment").checkSupportedResolutions();
        }
    };
    private Display mDefaultDisplay;
    private IllustrationPreference mImagePreference;
    private Set<Point> mResolutions;
    private Resources mResources;
    private String[] mScreenResolutionOptions;
    private String[] mScreenResolutionSummaries;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1920;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mDefaultDisplay = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(0);
        Resources resources = context.getResources();
        this.mResources = resources;
        this.mScreenResolutionOptions = resources.getStringArray(R$array.config_screen_resolution_options_strings);
        this.mScreenResolutionSummaries = this.mResources.getStringArray(R$array.config_screen_resolution_summaries_strings);
        this.mResolutions = getAllSupportedResolution();
        this.mImagePreference = new IllustrationPreference(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.screen_resolution_settings;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected void addStaticPreferences(PreferenceScreen preferenceScreen) {
        updateIllustrationImage(this.mImagePreference);
        preferenceScreen.addPreference(this.mImagePreference);
        FooterPreference footerPreference = new FooterPreference(preferenceScreen.getContext());
        footerPreference.setTitle(R$string.screen_resolution_footer);
        footerPreference.setSelectable(false);
        footerPreference.setLayoutResource(R$layout.preference_footer);
        preferenceScreen.addPreference(footerPreference);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    public void bindPreferenceExtra(SelectorWithWidgetPreference selectorWithWidgetPreference, String str, CandidateInfo candidateInfo, String str2, String str3) {
        CharSequence loadSummary = ((ScreenResolutionCandidateInfo) candidateInfo).loadSummary();
        if (loadSummary != null) {
            selectorWithWidgetPreference.setSummary(loadSummary);
        }
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        ArrayList arrayList = new ArrayList();
        int i = 0;
        while (true) {
            String[] strArr = this.mScreenResolutionOptions;
            if (i >= strArr.length) {
                return arrayList;
            }
            String str = strArr[i];
            arrayList.add(new ScreenResolutionCandidateInfo(str, this.mScreenResolutionSummaries[i], str, true));
            i++;
        }
    }

    private Set<Point> getAllSupportedResolution() {
        Display.Mode[] supportedModes;
        HashSet hashSet = new HashSet();
        for (Display.Mode mode : this.mDefaultDisplay.getSupportedModes()) {
            hashSet.add(new Point(mode.getPhysicalWidth(), mode.getPhysicalHeight()));
        }
        return hashSet;
    }

    private Display.Mode getPreferMode(int i) {
        for (Point point : this.mResolutions) {
            if (point.x == i) {
                return new Display.Mode(point.x, point.y, getDisplayMode().getRefreshRate());
            }
        }
        return getDisplayMode();
    }

    public Display.Mode getDisplayMode() {
        return this.mDefaultDisplay.getMode();
    }

    public void setDisplayMode(int i) {
        this.mDefaultDisplay.setUserPreferredDisplayMode(getPreferMode(i));
    }

    String getKeyForResolution(int i) {
        if (i == 1080) {
            return this.mScreenResolutionOptions[0];
        }
        if (i == 1440) {
            return this.mScreenResolutionOptions[1];
        }
        return null;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        return getKeyForResolution(getDisplayMode().getPhysicalWidth());
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        if (this.mScreenResolutionOptions[0].equals(str)) {
            setDisplayMode(1080);
        } else if (this.mScreenResolutionOptions[1].equals(str)) {
            setDisplayMode(1440);
        }
        updateIllustrationImage(this.mImagePreference);
        return true;
    }

    private void updateIllustrationImage(IllustrationPreference illustrationPreference) {
        String defaultKey = getDefaultKey();
        if (TextUtils.equals(this.mScreenResolutionOptions[0], defaultKey)) {
            illustrationPreference.setLottieAnimationResId(R$drawable.screen_resolution_1080p);
        } else if (TextUtils.equals(this.mScreenResolutionOptions[1], defaultKey)) {
            illustrationPreference.setLottieAnimationResId(R$drawable.screen_resolution_1440p);
        }
    }

    /* loaded from: classes.dex */
    public static class ScreenResolutionCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final CharSequence mLabel;
        private final CharSequence mSummary;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        ScreenResolutionCandidateInfo(CharSequence charSequence, CharSequence charSequence2, String str, boolean z) {
            super(z);
            this.mLabel = charSequence;
            this.mSummary = charSequence2;
            this.mKey = str;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mLabel;
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
