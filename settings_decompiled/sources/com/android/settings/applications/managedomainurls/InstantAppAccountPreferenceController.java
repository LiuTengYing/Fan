package com.android.settings.applications.managedomainurls;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.preference.Preference;
import com.android.settings.core.BasePreferenceController;
/* loaded from: classes.dex */
public class InstantAppAccountPreferenceController extends BasePreferenceController {
    private Intent mLaunchIntent;

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

    public InstantAppAccountPreferenceController(Context context, String str) {
        super(context, str);
        initAppSettingsIntent();
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return (this.mLaunchIntent == null || WebActionCategoryController.isDisableWebActions(this.mContext)) ? 3 : 0;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public boolean handlePreferenceTreeClick(Preference preference) {
        if (getPreferenceKey().equals(preference.getKey())) {
            Intent intent = this.mLaunchIntent;
            if (intent != null) {
                this.mContext.startActivity(intent);
                return true;
            }
            return true;
        }
        return false;
    }

    private void initAppSettingsIntent() {
        ComponentName instantAppResolverSettingsComponent = this.mContext.getPackageManager().getInstantAppResolverSettingsComponent();
        Intent component = instantAppResolverSettingsComponent != null ? new Intent().setComponent(instantAppResolverSettingsComponent) : null;
        if (component != null) {
            this.mLaunchIntent = component;
        }
    }
}
