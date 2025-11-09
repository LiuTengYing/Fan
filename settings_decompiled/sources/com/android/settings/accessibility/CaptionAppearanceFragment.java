package com.android.settings.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.CaptioningManager;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import com.android.internal.widget.SubtitleView;
import com.android.settings.R$array;
import com.android.settings.R$dimen;
import com.android.settings.R$id;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.accessibility.CaptionAppearanceFragment;
import com.android.settings.accessibility.ListDialogPreference;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.accessibility.AccessibilityUtils;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class CaptionAppearanceFragment extends DashboardFragment implements Preference.OnPreferenceChangeListener, ListDialogPreference.OnValueChangedListener {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.captioning_appearance);
    private ColorPreference mBackgroundColor;
    private ColorPreference mBackgroundOpacity;
    private CaptioningManager mCaptioningManager;
    private PreferenceCategory mCustom;
    private ColorPreference mEdgeColor;
    private EdgeTypePreference mEdgeType;
    private ListPreference mFontSize;
    private ColorPreference mForegroundColor;
    private ColorPreference mForegroundOpacity;
    private PresetPreference mPreset;
    private SubtitleView mPreviewText;
    private View mPreviewViewport;
    private View mPreviewWindow;
    private boolean mShowingCustom;
    private ListPreference mTypeface;
    private ColorPreference mWindowColor;
    private ColorPreference mWindowOpacity;
    private final List<Preference> mPreferenceList = new ArrayList();
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final View.OnLayoutChangeListener mLayoutChangeListener = new AnonymousClass1();

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "CaptionAppearanceFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1819;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.android.settings.accessibility.CaptionAppearanceFragment$1  reason: invalid class name */
    /* loaded from: classes.dex */
    public class AnonymousClass1 implements View.OnLayoutChangeListener {
        AnonymousClass1() {
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            CaptionAppearanceFragment.this.mPreviewViewport.removeOnLayoutChangeListener(this);
            CaptionAppearanceFragment.this.mHandler.post(new Runnable() { // from class: com.android.settings.accessibility.CaptionAppearanceFragment$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    CaptionAppearanceFragment.AnonymousClass1.this.lambda$onLayoutChange$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onLayoutChange$0() {
            CaptionAppearanceFragment.this.refreshPreviewText();
        }
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        this.mCaptioningManager = (CaptioningManager) getSystemService("captioning");
        initializeAllPreferences();
        updateAllPreferences();
        refreshShowingCustom();
        installUpdateListeners();
        refreshPreviewText();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.captioning_appearance;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshPreviewText() {
        SubtitleView subtitleView;
        FragmentActivity activity = getActivity();
        if (activity == null || (subtitleView = this.mPreviewText) == null) {
            return;
        }
        applyCaptionProperties(this.mCaptioningManager, subtitleView, this.mPreviewViewport, this.mCaptioningManager.getRawUserStyle());
        Locale locale = this.mCaptioningManager.getLocale();
        if (locale != null) {
            subtitleView.setText(AccessibilityUtils.getTextForLocale(activity, locale, R$string.captioning_preview_text));
        } else {
            subtitleView.setText(R$string.captioning_preview_text);
        }
        CaptioningManager.CaptionStyle userStyle = this.mCaptioningManager.getUserStyle();
        if (userStyle.hasWindowColor()) {
            this.mPreviewWindow.setBackgroundColor(userStyle.windowColor);
        } else {
            this.mPreviewWindow.setBackgroundColor(CaptioningManager.CaptionStyle.DEFAULT.windowColor);
        }
    }

    public static void applyCaptionProperties(CaptioningManager captioningManager, SubtitleView subtitleView, View view, int i) {
        subtitleView.setStyle(i);
        Context context = subtitleView.getContext();
        context.getContentResolver();
        float fontScale = captioningManager.getFontScale();
        if (view != null) {
            subtitleView.setTextSize((Math.max(view.getWidth() * 9, view.getHeight() * 16) / 16.0f) * 0.0533f * fontScale);
        } else {
            subtitleView.setTextSize(context.getResources().getDimension(R$dimen.caption_preview_text_size) * fontScale);
        }
        Locale locale = captioningManager.getLocale();
        if (locale != null) {
            subtitleView.setText(AccessibilityUtils.getTextForLocale(context, locale, R$string.captioning_preview_characters));
        } else {
            subtitleView.setText(R$string.captioning_preview_characters);
        }
    }

    private void initializeAllPreferences() {
        LayoutPreference layoutPreference = (LayoutPreference) findPreference("caption_preview");
        this.mPreviewText = layoutPreference.findViewById(R$id.preview_text);
        this.mPreviewWindow = layoutPreference.findViewById(R$id.preview_window);
        View findViewById = layoutPreference.findViewById(R$id.preview_viewport);
        this.mPreviewViewport = findViewById;
        findViewById.addOnLayoutChangeListener(this.mLayoutChangeListener);
        Resources resources = getResources();
        int[] intArray = resources.getIntArray(R$array.captioning_preset_selector_values);
        String[] stringArray = resources.getStringArray(R$array.captioning_preset_selector_titles);
        PresetPreference presetPreference = (PresetPreference) findPreference("captioning_preset");
        this.mPreset = presetPreference;
        presetPreference.setValues(intArray);
        this.mPreset.setTitles(stringArray);
        ListPreference listPreference = (ListPreference) findPreference("captioning_font_size");
        this.mFontSize = listPreference;
        this.mPreferenceList.add(listPreference);
        this.mPreferenceList.add(this.mPreset);
        this.mCustom = (PreferenceCategory) findPreference("custom");
        this.mShowingCustom = true;
        int[] intArray2 = resources.getIntArray(R$array.captioning_color_selector_values);
        String[] stringArray2 = resources.getStringArray(R$array.captioning_color_selector_titles);
        ColorPreference colorPreference = (ColorPreference) this.mCustom.findPreference("captioning_foreground_color");
        this.mForegroundColor = colorPreference;
        colorPreference.setTitles(stringArray2);
        this.mForegroundColor.setValues(intArray2);
        int[] intArray3 = resources.getIntArray(R$array.captioning_opacity_selector_values);
        String[] stringArray3 = resources.getStringArray(R$array.captioning_opacity_selector_titles);
        ColorPreference colorPreference2 = (ColorPreference) this.mCustom.findPreference("captioning_foreground_opacity");
        this.mForegroundOpacity = colorPreference2;
        colorPreference2.setTitles(stringArray3);
        this.mForegroundOpacity.setValues(intArray3);
        ColorPreference colorPreference3 = (ColorPreference) this.mCustom.findPreference("captioning_edge_color");
        this.mEdgeColor = colorPreference3;
        colorPreference3.setTitles(stringArray2);
        this.mEdgeColor.setValues(intArray2);
        int[] iArr = new int[intArray2.length + 1];
        String[] strArr = new String[stringArray2.length + 1];
        System.arraycopy(intArray2, 0, iArr, 1, intArray2.length);
        System.arraycopy(stringArray2, 0, strArr, 1, stringArray2.length);
        iArr[0] = 0;
        strArr[0] = getString(R$string.color_none);
        ColorPreference colorPreference4 = (ColorPreference) this.mCustom.findPreference("captioning_background_color");
        this.mBackgroundColor = colorPreference4;
        colorPreference4.setTitles(strArr);
        this.mBackgroundColor.setValues(iArr);
        ColorPreference colorPreference5 = (ColorPreference) this.mCustom.findPreference("captioning_background_opacity");
        this.mBackgroundOpacity = colorPreference5;
        colorPreference5.setTitles(stringArray3);
        this.mBackgroundOpacity.setValues(intArray3);
        ColorPreference colorPreference6 = (ColorPreference) this.mCustom.findPreference("captioning_window_color");
        this.mWindowColor = colorPreference6;
        colorPreference6.setTitles(strArr);
        this.mWindowColor.setValues(iArr);
        ColorPreference colorPreference7 = (ColorPreference) this.mCustom.findPreference("captioning_window_opacity");
        this.mWindowOpacity = colorPreference7;
        colorPreference7.setTitles(stringArray3);
        this.mWindowOpacity.setValues(intArray3);
        this.mEdgeType = (EdgeTypePreference) this.mCustom.findPreference("captioning_edge_type");
        this.mTypeface = (ListPreference) this.mCustom.findPreference("captioning_typeface");
    }

    private void installUpdateListeners() {
        this.mPreset.setOnValueChangedListener(this);
        this.mForegroundColor.setOnValueChangedListener(this);
        this.mForegroundOpacity.setOnValueChangedListener(this);
        this.mEdgeColor.setOnValueChangedListener(this);
        this.mBackgroundColor.setOnValueChangedListener(this);
        this.mBackgroundOpacity.setOnValueChangedListener(this);
        this.mWindowColor.setOnValueChangedListener(this);
        this.mWindowOpacity.setOnValueChangedListener(this);
        this.mEdgeType.setOnValueChangedListener(this);
        this.mTypeface.setOnPreferenceChangeListener(this);
        this.mFontSize.setOnPreferenceChangeListener(this);
    }

    private void updateAllPreferences() {
        this.mPreset.setValue(this.mCaptioningManager.getRawUserStyle());
        this.mFontSize.setValue(Float.toString(this.mCaptioningManager.getFontScale()));
        CaptioningManager.CaptionStyle customStyle = CaptioningManager.CaptionStyle.getCustomStyle(getContentResolver());
        this.mEdgeType.setValue(customStyle.edgeType);
        this.mEdgeColor.setValue(customStyle.edgeColor);
        parseColorOpacity(this.mForegroundColor, this.mForegroundOpacity, customStyle.hasForegroundColor() ? customStyle.foregroundColor : 16777215);
        parseColorOpacity(this.mBackgroundColor, this.mBackgroundOpacity, customStyle.hasBackgroundColor() ? customStyle.backgroundColor : 16777215);
        parseColorOpacity(this.mWindowColor, this.mWindowOpacity, customStyle.hasWindowColor() ? customStyle.windowColor : 16777215);
        String str = customStyle.mRawTypeface;
        ListPreference listPreference = this.mTypeface;
        if (str == null) {
            str = "";
        }
        listPreference.setValue(str);
    }

    private void parseColorOpacity(ColorPreference colorPreference, ColorPreference colorPreference2, int i) {
        int i2;
        int i3;
        if (!CaptioningManager.CaptionStyle.hasColor(i)) {
            i3 = (i & 255) << 24;
            i2 = 16777215;
        } else if ((i >>> 24) == 0) {
            i2 = 0;
            i3 = (i & 255) << 24;
        } else {
            i2 = i | (-16777216);
            i3 = (-16777216) & i;
        }
        colorPreference2.setValue(i3 | 16777215);
        colorPreference.setValue(i2);
    }

    private int mergeColorOpacity(ColorPreference colorPreference, ColorPreference colorPreference2) {
        int value = colorPreference.getValue();
        int value2 = colorPreference2.getValue();
        if (CaptioningManager.CaptionStyle.hasColor(value)) {
            return value == 0 ? Color.alpha(value2) : (value & 16777215) | (value2 & (-16777216));
        }
        return 16776960 | Color.alpha(value2);
    }

    private void refreshShowingCustom() {
        boolean z = this.mPreset.getValue() == -1;
        if (!z && this.mShowingCustom) {
            getPreferenceScreen().removePreference(this.mCustom);
            this.mShowingCustom = false;
        } else if (!z || this.mShowingCustom) {
        } else {
            getPreferenceScreen().addPreference(this.mCustom);
            this.mShowingCustom = true;
        }
    }

    @Override // com.android.settings.accessibility.ListDialogPreference.OnValueChangedListener
    public void onValueChanged(ListDialogPreference listDialogPreference, int i) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        ColorPreference colorPreference = this.mForegroundColor;
        if (colorPreference == listDialogPreference || this.mForegroundOpacity == listDialogPreference) {
            Settings.Secure.putInt(contentResolver, "accessibility_captioning_foreground_color", mergeColorOpacity(colorPreference, this.mForegroundOpacity));
        } else {
            ColorPreference colorPreference2 = this.mBackgroundColor;
            if (colorPreference2 == listDialogPreference || this.mBackgroundOpacity == listDialogPreference) {
                Settings.Secure.putInt(contentResolver, "accessibility_captioning_background_color", mergeColorOpacity(colorPreference2, this.mBackgroundOpacity));
            } else {
                ColorPreference colorPreference3 = this.mWindowColor;
                if (colorPreference3 == listDialogPreference || this.mWindowOpacity == listDialogPreference) {
                    Settings.Secure.putInt(contentResolver, "accessibility_captioning_window_color", mergeColorOpacity(colorPreference3, this.mWindowOpacity));
                } else if (this.mEdgeColor == listDialogPreference) {
                    Settings.Secure.putInt(contentResolver, "accessibility_captioning_edge_color", i);
                } else if (this.mPreset == listDialogPreference) {
                    Settings.Secure.putInt(contentResolver, "accessibility_captioning_preset", i);
                    refreshShowingCustom();
                } else if (this.mEdgeType == listDialogPreference) {
                    Settings.Secure.putInt(contentResolver, "accessibility_captioning_edge_type", i);
                }
            }
        }
        refreshPreviewText();
        enableCaptioningManager();
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        if (this.mTypeface == preference) {
            Settings.Secure.putString(contentResolver, "accessibility_captioning_typeface", (String) obj);
            refreshPreviewText();
            enableCaptioningManager();
            return true;
        } else if (this.mFontSize == preference) {
            Settings.Secure.putFloat(contentResolver, "accessibility_captioning_font_scale", Float.parseFloat((String) obj));
            refreshPreviewText();
            enableCaptioningManager();
            return true;
        } else {
            return true;
        }
    }

    private void enableCaptioningManager() {
        if (this.mCaptioningManager.isEnabled()) {
            return;
        }
        Settings.Secure.putInt(getContentResolver(), "accessibility_captioning_enabled", 1);
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_caption;
    }
}
