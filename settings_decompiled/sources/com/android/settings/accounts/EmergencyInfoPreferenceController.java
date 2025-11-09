package com.android.settings.accounts;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import androidx.preference.Preference;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.search.SearchIndexableRaw;
import java.util.List;
/* loaded from: classes.dex */
public class EmergencyInfoPreferenceController extends BasePreferenceController {
    Intent mIntent;

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

    public EmergencyInfoPreferenceController(Context context, String str) {
        super(context, str);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public void updateRawDataToIndex(List<SearchIndexableRaw> list) {
        if (isAvailable()) {
            SearchIndexableRaw searchIndexableRaw = new SearchIndexableRaw(this.mContext);
            Resources resources = this.mContext.getResources();
            int i = R$string.emergency_info_title;
            searchIndexableRaw.title = resources.getString(i);
            searchIndexableRaw.screenTitle = resources.getString(i);
            list.add(searchIndexableRaw);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        preference.setSummary(this.mContext.getString(R$string.emergency_info_summary, ((UserManager) this.mContext.getSystemService(UserManager.class)).getUserInfo(UserHandle.myUserId()).name));
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        Intent intent;
        if (!TextUtils.equals(getPreferenceKey(), preference.getKey()) || (intent = this.mIntent) == null) {
            return false;
        }
        intent.setFlags(67108864);
        this.mContext.startActivity(this.mIntent);
        return true;
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mContext.getResources().getBoolean(R$bool.config_show_emergency_info_in_device_info)) {
            return (isEmergencyInfoSupported() || isAOSPVersionSupported()) ? 0 : 3;
        }
        return 3;
    }

    private boolean isEmergencyInfoSupported() {
        this.mIntent = new Intent(this.mContext.getResources().getString(R$string.config_emergency_intent_action)).setPackage(this.mContext.getResources().getString(R$string.config_emergency_package_name));
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty()) ? false : true;
    }

    private boolean isAOSPVersionSupported() {
        this.mIntent = new Intent(this.mContext.getResources().getString(R$string.config_aosp_emergency_intent_action)).setPackage(this.mContext.getResources().getString(R$string.config_aosp_emergency_package_name));
        List<ResolveInfo> queryIntentActivities = this.mContext.getPackageManager().queryIntentActivities(this.mIntent, 0);
        return (queryIntentActivities == null || queryIntentActivities.isEmpty()) ? false : true;
    }
}
