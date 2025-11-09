package com.android.settings.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.hardware.display.ColorDisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.preference.PreferenceScreen;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.settings.R$drawable;
import com.android.settings.R$id;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.widget.RadioButtonPickerFragment;
import com.android.settingslib.widget.CandidateInfo;
import com.android.settingslib.widget.LayoutPreference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class ColorModePreferenceFragment extends RadioButtonPickerFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.color_mode_settings) { // from class: com.android.settings.display.ColorModePreferenceFragment.3
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.android.settings.search.BaseSearchIndexProvider
        public boolean isPageSearchEnabled(Context context) {
            int[] intArray = context.getResources().getIntArray(17235996);
            return (intArray == null || intArray.length <= 0 || ColorDisplayManager.areAccessibilityTransformsEnabled(context)) ? false : true;
        }
    };
    private ColorDisplayManager mColorDisplayManager;
    private ContentObserver mContentObserver;
    private ImageView[] mDotIndicators;
    private ArrayList<View> mPageList;
    private Resources mResources;
    private View mViewArrowNext;
    private View mViewArrowPrevious;
    private ViewPager mViewPager;
    private View[] mViewPagerImages;

    private boolean isValidColorMode(int i) {
        if (i == 0 || i == 1 || i == 2 || i == 3) {
            return true;
        }
        return i >= 256 && i <= 511;
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 1143;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mColorDisplayManager = (ColorDisplayManager) context.getSystemService(ColorDisplayManager.class);
        this.mResources = context.getResources();
        ContentResolver contentResolver = context.getContentResolver();
        this.mContentObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.settings.display.ColorModePreferenceFragment.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                if (ColorDisplayManager.areAccessibilityTransformsEnabled(ColorModePreferenceFragment.this.getContext())) {
                    ColorModePreferenceFragment.this.getActivity().finish();
                }
            }
        };
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_inversion_enabled"), false, this.mContentObserver, this.mUserId);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("accessibility_display_daltonizer_enabled"), false, this.mContentObserver, this.mUserId);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle != null) {
            int i = bundle.getInt("page_viewer_selection_index");
            this.mViewPager.setCurrentItem(i);
            updateIndicator(i);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onDetach() {
        if (this.mContentObserver != null) {
            getContext().getContentResolver().unregisterContentObserver(this.mContentObserver);
            this.mContentObserver = null;
        }
        super.onDetach();
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.preference.PreferenceFragmentCompat, androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt("page_viewer_selection_index", this.mViewPager.getCurrentItem());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.widget.RadioButtonPickerFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.color_mode_settings;
    }

    void configureAndInstallPreview(LayoutPreference layoutPreference, PreferenceScreen preferenceScreen) {
        layoutPreference.setSelectable(false);
        preferenceScreen.addPreference(layoutPreference);
    }

    public ArrayList<Integer> getViewPagerResource() {
        return new ArrayList<>(Arrays.asList(Integer.valueOf(R$layout.color_mode_view1), Integer.valueOf(R$layout.color_mode_view2), Integer.valueOf(R$layout.color_mode_view3)));
    }

    void addViewPager(LayoutPreference layoutPreference) {
        ArrayList<Integer> viewPagerResource = getViewPagerResource();
        this.mViewPager = (ViewPager) layoutPreference.findViewById(R$id.viewpager);
        this.mViewPagerImages = new View[3];
        for (int i = 0; i < viewPagerResource.size(); i++) {
            this.mViewPagerImages[i] = getLayoutInflater().inflate(viewPagerResource.get(i).intValue(), (ViewGroup) null);
        }
        ArrayList<View> arrayList = new ArrayList<>();
        this.mPageList = arrayList;
        arrayList.add(this.mViewPagerImages[0]);
        this.mPageList.add(this.mViewPagerImages[1]);
        this.mPageList.add(this.mViewPagerImages[2]);
        this.mViewPager.setAdapter(new ColorPagerAdapter(this.mPageList));
        View findViewById = layoutPreference.findViewById(R$id.arrow_previous);
        this.mViewArrowPrevious = findViewById;
        findViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.ColorModePreferenceFragment$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColorModePreferenceFragment.this.lambda$addViewPager$0(view);
            }
        });
        View findViewById2 = layoutPreference.findViewById(R$id.arrow_next);
        this.mViewArrowNext = findViewById2;
        findViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settings.display.ColorModePreferenceFragment$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                ColorModePreferenceFragment.this.lambda$addViewPager$1(view);
            }
        });
        this.mViewPager.addOnPageChangeListener(createPageListener());
        ViewGroup viewGroup = (ViewGroup) layoutPreference.findViewById(R$id.viewGroup);
        this.mDotIndicators = new ImageView[this.mPageList.size()];
        for (int i2 = 0; i2 < this.mPageList.size(); i2++) {
            ImageView imageView = new ImageView(getContext());
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(12, 12);
            marginLayoutParams.setMargins(6, 0, 6, 0);
            imageView.setLayoutParams(marginLayoutParams);
            this.mDotIndicators[i2] = imageView;
            viewGroup.addView(imageView);
        }
        updateIndicator(this.mViewPager.getCurrentItem());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addViewPager$0(View view) {
        this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() - 1, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addViewPager$1(View view) {
        this.mViewPager.setCurrentItem(this.mViewPager.getCurrentItem() + 1, true);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected void addStaticPreferences(PreferenceScreen preferenceScreen) {
        LayoutPreference layoutPreference = new LayoutPreference(preferenceScreen.getContext(), R$layout.color_mode_preview);
        configureAndInstallPreview(layoutPreference, preferenceScreen);
        addViewPager(layoutPreference);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected List<? extends CandidateInfo> getCandidates() {
        int[] intArray;
        Map<Integer, String> colorModeMapping = ColorModeUtils.getColorModeMapping(this.mResources);
        ArrayList arrayList = new ArrayList();
        for (int i : this.mResources.getIntArray(17235996)) {
            arrayList.add(new ColorModeCandidateInfo(colorModeMapping.get(Integer.valueOf(i)), getKeyForColorMode(i), true));
        }
        return arrayList;
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected String getDefaultKey() {
        int colorMode = getColorMode();
        if (isValidColorMode(colorMode)) {
            return getKeyForColorMode(colorMode);
        }
        return getKeyForColorMode(0);
    }

    @Override // com.android.settings.widget.RadioButtonPickerFragment
    protected boolean setDefaultKey(String str) {
        int parseInt = Integer.parseInt(str.substring(str.lastIndexOf("_") + 1));
        if (isValidColorMode(parseInt)) {
            setColorMode(parseInt);
        }
        return true;
    }

    public int getColorMode() {
        return this.mColorDisplayManager.getColorMode();
    }

    public void setColorMode(int i) {
        this.mColorDisplayManager.setColorMode(i);
    }

    String getKeyForColorMode(int i) {
        return "color_mode_" + i;
    }

    /* loaded from: classes.dex */
    static class ColorModeCandidateInfo extends CandidateInfo {
        private final String mKey;
        private final CharSequence mLabel;

        @Override // com.android.settingslib.widget.CandidateInfo
        public Drawable loadIcon() {
            return null;
        }

        ColorModeCandidateInfo(CharSequence charSequence, String str, boolean z) {
            super(z);
            this.mLabel = charSequence;
            this.mKey = str;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public CharSequence loadLabel() {
            return this.mLabel;
        }

        @Override // com.android.settingslib.widget.CandidateInfo
        public String getKey() {
            return this.mKey;
        }
    }

    private ViewPager.OnPageChangeListener createPageListener() {
        return new ViewPager.OnPageChangeListener() { // from class: com.android.settings.display.ColorModePreferenceFragment.2
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int i) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int i) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int i, float f, int i2) {
                if (f != 0.0f) {
                    for (int i3 = 0; i3 < ColorModePreferenceFragment.this.mPageList.size(); i3++) {
                        ColorModePreferenceFragment.this.mViewPagerImages[i3].setVisibility(0);
                    }
                    return;
                }
                ColorModePreferenceFragment.this.mViewPagerImages[i].setContentDescription(ColorModePreferenceFragment.this.getContext().getString(R$string.colors_viewpager_content_description));
                ColorModePreferenceFragment.this.updateIndicator(i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateIndicator(int i) {
        for (int i2 = 0; i2 < this.mPageList.size(); i2++) {
            if (i == i2) {
                this.mDotIndicators[i2].setBackgroundResource(R$drawable.ic_color_page_indicator_focused);
                this.mViewPagerImages[i2].setVisibility(0);
            } else {
                this.mDotIndicators[i2].setBackgroundResource(R$drawable.ic_color_page_indicator_unfocused);
                this.mViewPagerImages[i2].setVisibility(4);
            }
        }
        if (i == 0) {
            this.mViewArrowPrevious.setVisibility(4);
            this.mViewArrowNext.setVisibility(0);
        } else if (i == this.mPageList.size() - 1) {
            this.mViewArrowPrevious.setVisibility(0);
            this.mViewArrowNext.setVisibility(4);
        } else {
            this.mViewArrowPrevious.setVisibility(0);
            this.mViewArrowNext.setVisibility(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ColorPagerAdapter extends PagerAdapter {
        private final ArrayList<View> mPageViewList;

        @Override // androidx.viewpager.widget.PagerAdapter
        public boolean isViewFromObject(View view, Object obj) {
            return obj == view;
        }

        ColorPagerAdapter(ArrayList<View> arrayList) {
            this.mPageViewList = arrayList;
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            if (this.mPageViewList.get(i) != null) {
                viewGroup.removeView(this.mPageViewList.get(i));
            }
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            viewGroup.addView(this.mPageViewList.get(i));
            return this.mPageViewList.get(i);
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public int getCount() {
            return this.mPageViewList.size();
        }
    }
}
