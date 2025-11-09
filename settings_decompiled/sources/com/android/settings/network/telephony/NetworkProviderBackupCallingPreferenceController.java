package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.List;
/* loaded from: classes.dex */
public class NetworkProviderBackupCallingPreferenceController extends BasePreferenceController implements LifecycleObserver {
    private static final String KEY_PREFERENCE_CATEGORY = "provider_model_backup_calling_category";
    private static final String TAG = "NetProvBackupCallingCtrl";
    private NetworkProviderBackupCallingGroup mNetworkProviderBackupCallingGroup;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceScreen mPreferenceScreen;
    private List<SubscriptionInfo> mSubscriptionList;

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

    public NetworkProviderBackupCallingPreferenceController(Context context, String str) {
        super(context, str);
    }

    protected NetworkProviderBackupCallingGroup createBackupCallingControllerForSub(Lifecycle lifecycle, List<SubscriptionInfo> list) {
        return new NetworkProviderBackupCallingGroup(this.mContext, lifecycle, list, KEY_PREFERENCE_CATEGORY);
    }

    public void init(Lifecycle lifecycle) {
        List<SubscriptionInfo> activeSubscriptionList = getActiveSubscriptionList();
        this.mSubscriptionList = activeSubscriptionList;
        this.mNetworkProviderBackupCallingGroup = createBackupCallingControllerForSub(lifecycle, activeSubscriptionList);
    }

    private List<SubscriptionInfo> getActiveSubscriptionList() {
        return SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class));
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        List<SubscriptionInfo> list;
        return (this.mNetworkProviderBackupCallingGroup == null || (list = this.mSubscriptionList) == null || list.size() < 2) ? 2 : 0;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(KEY_PREFERENCE_CATEGORY);
        this.mPreferenceCategory = preferenceCategory;
        preferenceCategory.setVisible(isAvailable());
        this.mNetworkProviderBackupCallingGroup.displayPreference(preferenceScreen);
    }
}
