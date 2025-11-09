package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import com.android.settings.R$array;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.search.BaseSearchIndexProvider;
/* loaded from: classes.dex */
public class ToggleFontSizePreferenceFragment extends PreviewSeekBarPreferenceFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.android.settings.display.ToggleFontSizePreferenceFragment.1
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            return false;
        }
    };
    private float[] mValues;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 340;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    protected int getActivityLayoutResId() {
        return R$layout.font_size_activity;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    protected int[] getPreviewSampleResIds() {
        return new int[]{R$layout.font_size_preview};
    }

    @Override // com.android.settings.SettingsPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Resources resources = getContext().getResources();
        ContentResolver contentResolver = getContext().getContentResolver();
        this.mEntries = resources.getStringArray(R$array.entries_font_size);
        String[] stringArray = resources.getStringArray(R$array.entryvalues_font_size);
        this.mInitialIndex = fontSizeValueToIndex(Settings.System.getFloat(contentResolver, "font_scale", 1.0f), stringArray);
        this.mValues = new float[stringArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            this.mValues[i] = Float.parseFloat(stringArray[i]);
        }
        getActivity().setTitle(R$string.title_font_size);
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    protected Configuration createConfig(Configuration configuration, int i) {
        Configuration configuration2 = new Configuration(configuration);
        configuration2.fontScale = this.mValues[i];
        return configuration2;
    }

    @Override // com.android.settings.display.PreviewSeekBarPreferenceFragment
    protected void commit() {
        if (getContext() == null) {
            return;
        }
        Settings.System.putFloat(getContext().getContentResolver(), "font_scale", this.mValues[this.mCurrentIndex]);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_font_size;
    }

    public static int fontSizeValueToIndex(float f, String[] strArr) {
        float parseFloat = Float.parseFloat(strArr[0]);
        int i = 1;
        while (i < strArr.length) {
            float parseFloat2 = Float.parseFloat(strArr[i]);
            if (f < parseFloat + ((parseFloat2 - parseFloat) * 0.5f)) {
                return i - 1;
            }
            i++;
            parseFloat = parseFloat2;
        }
        return strArr.length - 1;
    }
}
