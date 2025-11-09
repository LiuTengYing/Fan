package com.android.settings.network;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionsPreferenceController;
import com.android.settings.wifi.WifiPickerTrackerHelper;
import com.android.settingslib.core.lifecycle.Lifecycle;
/* loaded from: classes.dex */
public class NetworkMobileProviderController extends BasePreferenceController implements SubscriptionsPreferenceController.UpdateListener {
    private static final int PREFERENCE_START_ORDER = 10;
    public static final String PREF_KEY_PROVIDER_MOBILE_NETWORK = "provider_model_mobile_network";
    private static final String TAG = "NetworkMobileProviderController";
    private boolean mHide;
    private int mOriginalExpandedChildrenCount;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceScreen mPreferenceScreen;
    private SubscriptionsPreferenceController mSubscriptionsController;

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

    public NetworkMobileProviderController(Context context, String str) {
        super(context, str);
    }

    public void init(Lifecycle lifecycle) {
        this.mSubscriptionsController = createSubscriptionsController(lifecycle);
    }

    SubscriptionsPreferenceController createSubscriptionsController(Lifecycle lifecycle) {
        SubscriptionsPreferenceController subscriptionsPreferenceController = this.mSubscriptionsController;
        return subscriptionsPreferenceController == null ? new SubscriptionsPreferenceController(this.mContext, lifecycle, this, PREF_KEY_PROVIDER_MOBILE_NETWORK, 10) : subscriptionsPreferenceController;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        if (this.mSubscriptionsController == null) {
            Log.e(TAG, "[displayPreference] SubscriptionsController is null.");
            return;
        }
        this.mPreferenceScreen = preferenceScreen;
        this.mOriginalExpandedChildrenCount = preferenceScreen.getInitialExpandedChildrenCount();
        PreferenceCategory preferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(PREF_KEY_PROVIDER_MOBILE_NETWORK);
        this.mPreferenceCategory = preferenceCategory;
        preferenceCategory.setVisible(isAvailable());
        this.mSubscriptionsController.displayPreference(preferenceScreen);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        SubscriptionsPreferenceController subscriptionsPreferenceController;
        return (this.mHide || (subscriptionsPreferenceController = this.mSubscriptionsController) == null || !subscriptionsPreferenceController.isAvailable()) ? 2 : 0;
    }

    @Override // com.android.settings.network.SubscriptionsPreferenceController.UpdateListener
    public void onChildrenUpdated() {
        boolean isAvailable = isAvailable();
        int i = this.mOriginalExpandedChildrenCount;
        if (i != Integer.MAX_VALUE) {
            if (isAvailable) {
                this.mPreferenceScreen.setInitialExpandedChildrenCount(i + this.mPreferenceCategory.getPreferenceCount());
            } else {
                this.mPreferenceScreen.setInitialExpandedChildrenCount(i);
            }
        }
        this.mPreferenceCategory.setVisible(isAvailable);
    }

    public void setWifiPickerTrackerHelper(WifiPickerTrackerHelper wifiPickerTrackerHelper) {
        SubscriptionsPreferenceController subscriptionsPreferenceController = this.mSubscriptionsController;
        if (subscriptionsPreferenceController != null) {
            subscriptionsPreferenceController.setWifiPickerTrackerHelper(wifiPickerTrackerHelper);
        }
    }

    public void hidePreference(boolean z, boolean z2) {
        this.mHide = z;
        if (z2) {
            this.mPreferenceCategory.setVisible(z ? false : isAvailable());
        }
    }
}
