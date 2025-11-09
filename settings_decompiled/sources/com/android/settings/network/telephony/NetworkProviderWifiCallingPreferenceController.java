package com.android.settings.network.telephony;

import android.content.Context;
import android.content.IntentFilter;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.network.SubscriptionUtil;
import com.android.settings.network.telephony.WifiCallingContentObserver;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class NetworkProviderWifiCallingPreferenceController extends BasePreferenceController implements LifecycleObserver, WifiCallingContentObserver.WifiCallingContentObserverClient {
    private static final String PREFERENCE_CATEGORY_KEY = "provider_model_calling_category";
    private static final String TAG = "NetworkProviderWfcController";
    private NetworkProviderWifiCallingGroup mNetworkProviderWifiCallingGroup;
    private PreferenceCategory mPreferenceCategory;
    private PreferenceScreen mPreferenceScreen;
    private ArrayList<SubscriptionInfo> mSubInfoList;
    private WifiCallingContentObserver mWifiCallingContentObserver;

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

    public NetworkProviderWifiCallingPreferenceController(Context context, String str) {
        super(context, str);
        this.mSubInfoList = new ArrayList<>();
        this.mWifiCallingContentObserver = null;
        this.mWifiCallingContentObserver = new WifiCallingContentObserver(context, this);
    }

    public void init(Lifecycle lifecycle) {
        this.mNetworkProviderWifiCallingGroup = createWifiCallingControllerForSub(lifecycle);
        this.mSubInfoList = new ArrayList<>(SubscriptionUtil.getActiveSubscriptions((SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class)));
        lifecycle.addObserver(this);
    }

    protected NetworkProviderWifiCallingGroup createWifiCallingControllerForSub(Lifecycle lifecycle) {
        return new NetworkProviderWifiCallingGroup(this.mContext, lifecycle, PREFERENCE_CATEGORY_KEY);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        NetworkProviderWifiCallingGroup networkProviderWifiCallingGroup = this.mNetworkProviderWifiCallingGroup;
        return (networkProviderWifiCallingGroup == null || !networkProviderWifiCallingGroup.isAvailable()) ? 3 : 0;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        PreferenceScreen preferenceScreen;
        if (this.mSubInfoList == null || (preferenceScreen = this.mPreferenceScreen) == null) {
            return;
        }
        displayPreference(preferenceScreen);
        this.mSubInfoList.stream().filter(new Predicate() { // from class: com.android.settings.network.telephony.NetworkProviderWifiCallingPreferenceController$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((SubscriptionInfo) obj);
            }
        }).forEach(new Consumer() { // from class: com.android.settings.network.telephony.NetworkProviderWifiCallingPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                NetworkProviderWifiCallingPreferenceController.this.lambda$onStart$0((SubscriptionInfo) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onStart$0(SubscriptionInfo subscriptionInfo) {
        this.mWifiCallingContentObserver.startForSubid(subscriptionInfo.getSubscriptionId());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mWifiCallingContentObserver.stop();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreferenceScreen = preferenceScreen;
        this.mPreferenceCategory = (PreferenceCategory) preferenceScreen.findPreference(PREFERENCE_CATEGORY_KEY);
        this.mNetworkProviderWifiCallingGroup.displayPreference(preferenceScreen);
        this.mPreferenceCategory.setVisible(isAvailable());
    }

    @Override // com.android.settings.network.telephony.WifiCallingContentObserver.WifiCallingContentObserverClient
    public void onWfcStateChanged() {
        PreferenceScreen preferenceScreen = this.mPreferenceScreen;
        if (preferenceScreen != null) {
            displayPreference(preferenceScreen);
        }
    }
}
