package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.android.settings.R$drawable;
import com.android.settings.R$layout;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class GestureRecognitionPlayFragment extends DashboardFragment {
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER;
    static final int[] img;
    private static GestureRecognitionPlayPreferenceController mGestureRecognitionPlayPreferenceController;
    static final int[] summary;
    static final int[] title;
    private ImageViewPagerPreference mImageViewPagerPreference;
    private List<ImageViewMode> viewList;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "GestureRecognitionPlayFragment";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50955;
    }

    static {
        int i = R$string.text_play_title;
        title = new int[]{i, i};
        summary = new int[]{R$string.text_play_summary, R$string.text_stop_summary};
        img = new int[]{R$drawable.gesture_recognition_play, R$drawable.gesture_recognition_stop};
        SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider(R$xml.gesture_recognition_play);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment, androidx.preference.PreferenceFragmentCompat
    public void onCreatePreferences(Bundle bundle, String str) {
        super.onCreatePreferences(bundle, str);
        initializeAllPreferences();
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        GestureRecognitionPlayPreferenceController gestureRecognitionPlayPreferenceController = new GestureRecognitionPlayPreferenceController(context, fragment);
        mGestureRecognitionPlayPreferenceController = gestureRecognitionPlayPreferenceController;
        arrayList.add(gestureRecognitionPlayPreferenceController);
        return arrayList;
    }

    private void initializeAllPreferences() {
        this.mImageViewPagerPreference = (ImageViewPagerPreference) findPreference("gesture_preview");
        this.mImageViewPagerPreference.setPreviewAdapter(new ImageViewPagerAdapter(initLayout()));
    }

    public List<ImageViewMode> initLayout() {
        this.viewList = new ArrayList();
        int i = 0;
        while (true) {
            int[] iArr = title;
            if (i < iArr.length) {
                this.viewList.add(new ImageViewMode(getActivity().getLayoutInflater().inflate(R$layout.image_priview, (ViewGroup) null), img[i], iArr[i], summary[i]));
                i++;
            } else {
                return this.viewList;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.gesture_recognition_play;
    }
}
