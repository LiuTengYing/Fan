package com.android.settings.slices;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.provider.SearchIndexableResource;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import com.android.settings.R$array;
import com.android.settings.R$drawable;
import com.android.settings.R$string;
import com.android.settings.accessibility.AccessibilitySettings;
import com.android.settings.accessibility.AccessibilitySlicePreferenceController;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.slices.SliceData;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.search.Indexable$SearchIndexProvider;
import com.android.settingslib.search.SearchIndexableData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes.dex */
class SliceDataConverter {
    private Context mContext;
    private final MetricsFeatureProvider mMetricsFeatureProvider;

    public SliceDataConverter(Context context) {
        this.mContext = context;
        this.mMetricsFeatureProvider = FeatureFactory.getFactory(context).getMetricsFeatureProvider();
    }

    public List<SliceData> getSliceData() {
        ArrayList arrayList = new ArrayList();
        for (SearchIndexableData searchIndexableData : FeatureFactory.getFactory(this.mContext).getSearchFeatureProvider().getSearchIndexableResources().getProviderValues()) {
            String name = searchIndexableData.getTargetClass().getName();
            Indexable$SearchIndexProvider searchIndexProvider = searchIndexableData.getSearchIndexProvider();
            if (searchIndexProvider == null) {
                Log.e("SliceDataConverter", name + " dose not implement Search Index Provider");
            } else {
                arrayList.addAll(getSliceDataFromProvider(searchIndexProvider, name));
            }
        }
        arrayList.addAll(getAccessibilitySliceData());
        return arrayList;
    }

    private List<SliceData> getSliceDataFromProvider(Indexable$SearchIndexProvider indexable$SearchIndexProvider, String str) {
        ArrayList arrayList = new ArrayList();
        List<SearchIndexableResource> xmlResourcesToIndex = indexable$SearchIndexProvider.getXmlResourcesToIndex(this.mContext, true);
        if (xmlResourcesToIndex == null) {
            return arrayList;
        }
        for (SearchIndexableResource searchIndexableResource : xmlResourcesToIndex) {
            int i = searchIndexableResource.xmlResId;
            if (i == 0) {
                Log.e("SliceDataConverter", str + " provides invalid XML (0) in search provider.");
            } else {
                arrayList.addAll(getSliceDataFromXML(i, str));
            }
        }
        return arrayList;
    }

    /* JADX WARN: Code restructure failed: missing block: B:35:0x013e, code lost:
        if (0 == 0) goto L32;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x017d, code lost:
        if (0 == 0) goto L32;
     */
    /* JADX WARN: Removed duplicated region for block: B:51:0x0185  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private java.util.List<com.android.settings.slices.SliceData> getSliceDataFromXML(int r17, java.lang.String r18) {
        /*
            Method dump skipped, instructions count: 393
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.slices.SliceDataConverter.getSliceDataFromXML(int, java.lang.String):java.util.List");
    }

    private List<SliceData> getAccessibilitySliceData() {
        ArrayList arrayList = new ArrayList();
        String name = AccessibilitySlicePreferenceController.class.getName();
        String name2 = AccessibilitySettings.class.getName();
        SliceData.Builder preferenceControllerClassName = new SliceData.Builder().setFragmentName(name2).setScreenTitle(this.mContext.getText(R$string.accessibility_settings)).setPreferenceControllerClassName(name);
        HashSet hashSet = new HashSet();
        Collections.addAll(hashSet, this.mContext.getResources().getStringArray(R$array.config_settings_slices_accessibility_components));
        List<AccessibilityServiceInfo> accessibilityServiceInfoList = getAccessibilityServiceInfoList();
        PackageManager packageManager = this.mContext.getPackageManager();
        for (AccessibilityServiceInfo accessibilityServiceInfo : accessibilityServiceInfoList) {
            ResolveInfo resolveInfo = accessibilityServiceInfo.getResolveInfo();
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            String flattenToString = new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToString();
            if (hashSet.contains(flattenToString)) {
                String charSequence = resolveInfo.loadLabel(packageManager).toString();
                int iconResource = resolveInfo.getIconResource();
                if (iconResource == 0) {
                    iconResource = R$drawable.ic_accessibility_generic;
                }
                preferenceControllerClassName.setKey(flattenToString).setTitle(charSequence).setUri(new Uri.Builder().scheme("content").authority("com.android.settings.slices").appendPath("action").appendPath(flattenToString).build()).setIcon(iconResource).setSliceType(1);
                try {
                    arrayList.add(preferenceControllerClassName.build());
                } catch (SliceData.InvalidSliceDataException e) {
                    Log.w("SliceDataConverter", "Invalid data when building a11y SliceData for " + flattenToString, e);
                }
            }
        }
        return arrayList;
    }

    List<AccessibilityServiceInfo> getAccessibilityServiceInfoList() {
        return AccessibilityManager.getInstance(this.mContext).getInstalledAccessibilityServiceList();
    }
}
