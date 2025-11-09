package com.android.settings.wifi.savedaccesspoints2;

import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.wifi.WifiEntryPreference;
import com.android.wifitrackerlib.WifiEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes2.dex */
public class SavedAccessPointsPreferenceController2 extends BasePreferenceController implements Preference.OnPreferenceClickListener {
    private SavedAccessPointsWifiSettings2 mHost;
    private PreferenceGroup mPreferenceGroup;
    List<WifiEntry> mWifiEntries;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public SavedAccessPointsPreferenceController2(Context context, String str) {
        super(context, str);
        this.mWifiEntries = new ArrayList();
    }

    public SavedAccessPointsPreferenceController2 setHost(SavedAccessPointsWifiSettings2 savedAccessPointsWifiSettings2) {
        this.mHost = savedAccessPointsWifiSettings2;
        return this;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return this.mWifiEntries.size() > 0 ? 0 : 2;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        this.mPreferenceGroup = (PreferenceGroup) preferenceScreen.findPreference(getPreferenceKey());
        updatePreference();
        super.displayPreference(preferenceScreen);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void displayPreference(PreferenceScreen preferenceScreen, List<WifiEntry> list) {
        if (list == null || list.isEmpty()) {
            this.mWifiEntries.clear();
        } else {
            this.mWifiEntries = list;
        }
        displayPreference(preferenceScreen);
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        SavedAccessPointsWifiSettings2 savedAccessPointsWifiSettings2 = this.mHost;
        if (savedAccessPointsWifiSettings2 != null) {
            savedAccessPointsWifiSettings2.showWifiPage(preference.getKey(), preference.getTitle());
            return false;
        }
        return false;
    }

    private void updatePreference() {
        ArrayList<String> arrayList = new ArrayList();
        int preferenceCount = this.mPreferenceGroup.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            final String key = this.mPreferenceGroup.getPreference(i).getKey();
            if (this.mWifiEntries.stream().filter(new Predicate() { // from class: com.android.settings.wifi.savedaccesspoints2.SavedAccessPointsPreferenceController2$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updatePreference$0;
                    lambda$updatePreference$0 = SavedAccessPointsPreferenceController2.lambda$updatePreference$0(key, (WifiEntry) obj);
                    return lambda$updatePreference$0;
                }
            }).count() == 0) {
                arrayList.add(key);
            }
        }
        for (String str : arrayList) {
            PreferenceGroup preferenceGroup = this.mPreferenceGroup;
            preferenceGroup.removePreference(preferenceGroup.findPreference(str));
        }
        for (WifiEntry wifiEntry : this.mWifiEntries) {
            if (this.mPreferenceGroup.findPreference(wifiEntry.getKey()) == null) {
                WifiEntryPreference wifiEntryPreference = new WifiEntryPreference(this.mContext, wifiEntry);
                wifiEntryPreference.setKey(wifiEntry.getKey());
                wifiEntryPreference.setOnPreferenceClickListener(this);
                this.mPreferenceGroup.addPreference(wifiEntryPreference);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$updatePreference$0(String str, WifiEntry wifiEntry) {
        return TextUtils.equals(str, wifiEntry.getKey());
    }
}
