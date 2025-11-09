package com.android.settings.bluetooth;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityShortcutInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import android.view.accessibility.AccessibilityManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;
import com.android.net.module.util.CollectionUtils;
import com.android.settings.accessibility.RestrictedPreferenceHelper;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.bluetooth.CachedBluetoothDevice;
import com.android.settingslib.core.lifecycle.Lifecycle;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class BluetoothDetailsRelatedToolsController extends BluetoothDetailsController {
    private PreferenceCategory mPreferenceCategory;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "bluetooth_related_tools";
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void refresh() {
    }

    public BluetoothDetailsRelatedToolsController(Context context, PreferenceFragmentCompat preferenceFragmentCompat, CachedBluetoothDevice cachedBluetoothDevice, Lifecycle lifecycle) {
        super(context, preferenceFragmentCompat, cachedBluetoothDevice, lifecycle);
        lifecycle.addObserver(this);
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController, com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return this.mCachedDevice.isHearingAidDevice();
    }

    @Override // com.android.settings.bluetooth.BluetoothDetailsController
    protected void init(PreferenceScreen preferenceScreen) {
        if (this.mCachedDevice.isHearingAidDevice()) {
            this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(getPreferenceKey());
            Preference findPreference = preferenceScreen.findPreference("live_caption");
            if (!findPreference.isVisible()) {
                this.mPreferenceCategory.removePreference(findPreference);
            }
            List<ComponentName> relatedTools = FeatureFactory.getFactory(((BluetoothDetailsController) this).mContext).getBluetoothFeatureProvider().getRelatedTools();
            if (!CollectionUtils.isEmpty(relatedTools)) {
                addAccessibilityInstalledRelatedPreference(relatedTools);
            }
            if (this.mPreferenceCategory.getPreferenceCount() == 0) {
                preferenceScreen.removePreference(this.mPreferenceCategory);
            }
        }
    }

    private void addAccessibilityInstalledRelatedPreference(final List<ComponentName> list) {
        AccessibilityManager accessibilityManager = AccessibilityManager.getInstance(((BluetoothDetailsController) this).mContext);
        RestrictedPreferenceHelper restrictedPreferenceHelper = new RestrictedPreferenceHelper(((BluetoothDetailsController) this).mContext);
        for (RestrictedPreference restrictedPreference : (List) Stream.of((Object[]) new List[]{restrictedPreferenceHelper.createAccessibilityServicePreferenceList((List) accessibilityManager.getInstalledAccessibilityServiceList().stream().filter(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$addAccessibilityInstalledRelatedPreference$0;
                lambda$addAccessibilityInstalledRelatedPreference$0 = BluetoothDetailsRelatedToolsController.lambda$addAccessibilityInstalledRelatedPreference$0(list, (AccessibilityServiceInfo) obj);
                return lambda$addAccessibilityInstalledRelatedPreference$0;
            }
        }).collect(Collectors.toList())), restrictedPreferenceHelper.createAccessibilityActivityPreferenceList((List) accessibilityManager.getInstalledAccessibilityShortcutListAsUser(((BluetoothDetailsController) this).mContext, UserHandle.myUserId()).stream().filter(new Predicate() { // from class: com.android.settings.bluetooth.BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$addAccessibilityInstalledRelatedPreference$1;
                lambda$addAccessibilityInstalledRelatedPreference$1 = BluetoothDetailsRelatedToolsController.lambda$addAccessibilityInstalledRelatedPreference$1(list, (AccessibilityShortcutInfo) obj);
                return lambda$addAccessibilityInstalledRelatedPreference$1;
            }
        }).collect(Collectors.toList()))}).flatMap(new BluetoothDetailsRelatedToolsController$$ExternalSyntheticLambda2()).collect(Collectors.toList())) {
            restrictedPreference.setOrder(99);
            this.mPreferenceCategory.addPreference(restrictedPreference);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$addAccessibilityInstalledRelatedPreference$0(List list, AccessibilityServiceInfo accessibilityServiceInfo) {
        return list.contains(accessibilityServiceInfo.getComponentName());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$addAccessibilityInstalledRelatedPreference$1(List list, AccessibilityShortcutInfo accessibilityShortcutInfo) {
        return list.contains(accessibilityShortcutInfo.getComponentName());
    }
}
