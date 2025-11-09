package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.widget.PreferenceCategoryController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
/* loaded from: classes.dex */
public class NetworkProviderDownloadedSimsCategoryController extends PreferenceCategoryController implements LifecycleObserver {
    private static final String KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM = "provider_model_downloaded_sim_category";
    private static final String LOG_TAG = "NetworkProviderDownloadedSimsCategoryController";
    private NetworkProviderDownloadedSimListController mNetworkProviderDownloadedSimListController;
    private PreferenceCategory mPreferenceCategory;

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ int getSliceHighlightMenuRes() {
        return super.getSliceHighlightMenuRes();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isPublicSlice() {
        return super.isPublicSlice();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean isSliceable() {
        return super.isSliceable();
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }

    public NetworkProviderDownloadedSimsCategoryController(Context context, String str, Lifecycle lifecycle) {
        super(context, str);
        this.mNetworkProviderDownloadedSimListController = new NetworkProviderDownloadedSimListController(this.mContext, lifecycle);
    }

    @Override // com.android.settings.widget.PreferenceCategoryController, com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        NetworkProviderDownloadedSimListController networkProviderDownloadedSimListController = this.mNetworkProviderDownloadedSimListController;
        return (networkProviderDownloadedSimListController == null || !networkProviderDownloadedSimListController.isAvailable()) ? 2 : 0;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mNetworkProviderDownloadedSimListController.displayPreference(preferenceScreen);
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY_DOWNLOADED_SIM);
        this.mPreferenceCategory = preferenceCategory;
        if (preferenceCategory == null) {
            Log.d(LOG_TAG, "displayPreference(), Can not find the category.");
        } else {
            preferenceCategory.setVisible(isAvailable());
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        int i;
        super.updateState(preference);
        PreferenceCategory preferenceCategory = this.mPreferenceCategory;
        if (preferenceCategory == null) {
            Log.d(LOG_TAG, "updateState(), Can not find the category.");
            return;
        }
        int preferenceCount = preferenceCategory.getPreferenceCount();
        Context context = this.mContext;
        if (preferenceCount > 1) {
            i = R$string.downloaded_sims_category_title;
        } else {
            i = R$string.downloaded_sim_category_title;
        }
        this.mPreferenceCategory.setTitle(context.getString(i));
    }
}
