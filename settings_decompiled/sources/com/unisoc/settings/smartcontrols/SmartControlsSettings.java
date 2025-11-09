package com.unisoc.settings.smartcontrols;

import android.content.Context;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import androidx.fragment.app.Fragment;
import com.android.settings.R$xml;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public class SmartControlsSettings extends DashboardFragment {
    public static final Indexable$SearchIndexProvider SEARCH_INDEX_DATA_PROVIDER = new BaseSearchIndexProvider() { // from class: com.unisoc.settings.smartcontrols.SmartControlsSettings.1
        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
            ArrayList arrayList = new ArrayList();
            if (UserHandle.myUserId() == 0 && Utils.isSupportSmartControl(context)) {
                SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
                searchIndexableResource.xmlResId = R$xml.smart_controls_settings;
                arrayList.add(searchIndexableResource);
            }
            return arrayList;
        }

        @Override // com.android.settings.search.BaseSearchIndexProvider, com.android.settingslib.search.Indexable$SearchIndexProvider
        public List<String> getNonIndexableKeys(Context context) {
            List<String> nonIndexableKeys = super.getNonIndexableKeys(context);
            if (!SmartWakePreferenceController.isSmartWakeAvailable(context) || !SmartWakePreferenceController.isWakeGestureAvailable(context)) {
                nonIndexableKeys.add("smart_wake");
            }
            if (!SmartMotionPreferenceController.isSmartMotionAvailable(context)) {
                nonIndexableKeys.add("smart_motion");
            }
            if (!PocketModePreferenceController.isPocketModeAvailable(context)) {
                nonIndexableKeys.add("pocket_mode");
            }
            if (!SmartPickUpPreferenceController.isSmartPickUpAvailable(context)) {
                nonIndexableKeys.add("smart_pick_up");
            }
            return nonIndexableKeys;
        }
    };

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment
    public String getLogTag() {
        return "SmartControlsSettings";
    }

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 50951;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settings.dashboard.DashboardFragment, com.android.settings.core.InstrumentedPreferenceFragment
    public int getPreferenceScreenResId() {
        return R$xml.smart_controls_settings;
    }

    @Override // com.android.settings.dashboard.DashboardFragment
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(Context context, Fragment fragment) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new SmartWakePreferenceController(context, fragment));
        arrayList.add(new SmartPickUpPreferenceController(context, fragment));
        arrayList.add(new SmartMotionPreferenceController(context));
        arrayList.add(new PocketModePreferenceController(context));
        arrayList.add(new GestureRecognitionPreferenceController(context));
        return arrayList;
    }

    public static boolean isSupportSmartControl(Context context) {
        return Utils.isSupportSmartControl(context);
    }
}
