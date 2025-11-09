package com.android.settings.search;

import android.content.Context;
import android.os.Bundle;
import android.provider.SearchIndexableResource;
import android.util.Log;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerListHelper;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.core.PreferenceXmlParserUtils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableRaw;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class BaseSearchIndexProvider implements Indexable$SearchIndexProvider {
    private int mXmlRes;

    public List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return null;
    }

    @Override // com.android.settingslib.search.Indexable$SearchIndexProvider
    public List<SearchIndexableRaw> getRawDataToIndex(Context context, boolean z) {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isPageSearchEnabled(Context context) {
        return true;
    }

    public BaseSearchIndexProvider() {
        this.mXmlRes = 0;
    }

    public BaseSearchIndexProvider(int i) {
        this.mXmlRes = i;
    }

    @Override // com.android.settingslib.search.Indexable$SearchIndexProvider
    public List<SearchIndexableResource> getXmlResourcesToIndex(Context context, boolean z) {
        if (this.mXmlRes != 0) {
            SearchIndexableResource searchIndexableResource = new SearchIndexableResource(context);
            searchIndexableResource.xmlResId = this.mXmlRes;
            return Arrays.asList(searchIndexableResource);
        }
        return null;
    }

    @Override // com.android.settingslib.search.Indexable$SearchIndexProvider
    public List<SearchIndexableRaw> getDynamicRawDataToIndex(Context context, boolean z) {
        List<AbstractPreferenceController> preferenceControllers;
        ArrayList arrayList = new ArrayList();
        if (isPageSearchEnabled(context) && (preferenceControllers = getPreferenceControllers(context)) != null && !preferenceControllers.isEmpty()) {
            for (AbstractPreferenceController abstractPreferenceController : preferenceControllers) {
                if (abstractPreferenceController instanceof PreferenceControllerMixin) {
                    ((PreferenceControllerMixin) abstractPreferenceController).updateDynamicRawDataToIndex(arrayList);
                } else if (abstractPreferenceController instanceof BasePreferenceController) {
                    ((BasePreferenceController) abstractPreferenceController).updateDynamicRawDataToIndex(arrayList);
                } else {
                    Log.e("BaseSearchIndex", abstractPreferenceController.getClass().getName() + " must implement " + PreferenceControllerMixin.class.getName() + " treating the dynamic indexable");
                }
            }
        }
        return arrayList;
    }

    @Override // com.android.settingslib.search.Indexable$SearchIndexProvider
    public List<String> getNonIndexableKeys(Context context) {
        if (!isPageSearchEnabled(context)) {
            return getNonIndexableKeysFromXml(context, true);
        }
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(getNonIndexableKeysFromXml(context, false));
        List<AbstractPreferenceController> preferenceControllers = getPreferenceControllers(context);
        if (preferenceControllers != null && !preferenceControllers.isEmpty()) {
            for (AbstractPreferenceController abstractPreferenceController : preferenceControllers) {
                if (abstractPreferenceController instanceof PreferenceControllerMixin) {
                    ((PreferenceControllerMixin) abstractPreferenceController).updateNonIndexableKeys(arrayList);
                } else if (abstractPreferenceController instanceof BasePreferenceController) {
                    ((BasePreferenceController) abstractPreferenceController).updateNonIndexableKeys(arrayList);
                } else {
                    Log.e("BaseSearchIndex", abstractPreferenceController.getClass().getName() + " must implement " + PreferenceControllerMixin.class.getName() + " treating the key non-indexable");
                    arrayList.add(abstractPreferenceController.getPreferenceKey());
                }
            }
        }
        return arrayList;
    }

    public List<AbstractPreferenceController> getPreferenceControllers(Context context) {
        List<AbstractPreferenceController> arrayList = new ArrayList<>();
        try {
            arrayList = createPreferenceControllers(context);
        } catch (Exception unused) {
            Log.w("BaseSearchIndex", "Error initial controller");
        }
        List<SearchIndexableResource> xmlResourcesToIndex = getXmlResourcesToIndex(context, true);
        if (xmlResourcesToIndex == null || xmlResourcesToIndex.isEmpty()) {
            return arrayList;
        }
        ArrayList arrayList2 = new ArrayList();
        for (SearchIndexableResource searchIndexableResource : xmlResourcesToIndex) {
            arrayList2.addAll(PreferenceControllerListHelper.getPreferenceControllersFromXml(context, searchIndexableResource.xmlResId));
        }
        List<BasePreferenceController> filterControllers = PreferenceControllerListHelper.filterControllers(arrayList2, arrayList);
        ArrayList arrayList3 = new ArrayList();
        if (arrayList != null) {
            arrayList3.addAll(arrayList);
        }
        arrayList3.addAll(filterControllers);
        return arrayList3;
    }

    private List<String> getNonIndexableKeysFromXml(Context context, boolean z) {
        List<SearchIndexableResource> xmlResourcesToIndex = getXmlResourcesToIndex(context, true);
        if (xmlResourcesToIndex == null || xmlResourcesToIndex.isEmpty()) {
            return new ArrayList();
        }
        ArrayList arrayList = new ArrayList();
        for (SearchIndexableResource searchIndexableResource : xmlResourcesToIndex) {
            arrayList.addAll(getNonIndexableKeysFromXml(context, searchIndexableResource.xmlResId, z));
        }
        return arrayList;
    }

    public List<String> getNonIndexableKeysFromXml(Context context, int i, boolean z) {
        return getKeysFromXml(context, i, z);
    }

    private List<String> getKeysFromXml(Context context, int i, boolean z) {
        ArrayList arrayList = new ArrayList();
        try {
            for (Bundle bundle : PreferenceXmlParserUtils.extractMetadata(context, i, 515)) {
                if (z || !bundle.getBoolean("searchable", true)) {
                    arrayList.add(bundle.getString("key"));
                }
            }
        } catch (IOException | XmlPullParserException unused) {
            Log.w("BaseSearchIndex", "Error parsing non-indexable from xml " + i);
        }
        return arrayList;
    }
}
