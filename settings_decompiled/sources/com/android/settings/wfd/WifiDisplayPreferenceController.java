package com.android.settings.wfd;

import android.content.Context;
import android.content.IntentFilter;
import android.media.MediaRouter;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class WifiDisplayPreferenceController extends BasePreferenceController implements LifecycleObserver, OnStart, OnStop {
    private Preference mPreference;
    private final MediaRouter mRouter;
    private final MediaRouter.Callback mRouterCallback;

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

    public WifiDisplayPreferenceController(Context context, String str) {
        super(context, str);
        this.mRouterCallback = new MediaRouter.SimpleCallback() { // from class: com.android.settings.wfd.WifiDisplayPreferenceController.1
            @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
            public void onRouteSelected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
                WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
                wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
            }

            @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
            public void onRouteUnselected(MediaRouter mediaRouter, int i, MediaRouter.RouteInfo routeInfo) {
                WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
                wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
            }

            @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
            public void onRouteAdded(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
                wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
            }

            @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
            public void onRouteRemoved(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
                wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
            }

            @Override // android.media.MediaRouter.SimpleCallback, android.media.MediaRouter.Callback
            public void onRouteChanged(MediaRouter mediaRouter, MediaRouter.RouteInfo routeInfo) {
                WifiDisplayPreferenceController wifiDisplayPreferenceController = WifiDisplayPreferenceController.this;
                wifiDisplayPreferenceController.refreshSummary(wifiDisplayPreferenceController.mPreference);
            }
        };
        MediaRouter mediaRouter = (MediaRouter) context.getSystemService(MediaRouter.class);
        this.mRouter = mediaRouter;
        mediaRouter.setRouterGroupId("android.media.mirroring_group");
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        return WifiDisplaySettings.isAvailable(this.mContext) ? 0 : 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        String string = this.mContext.getString(R$string.disconnected);
        int routeCount = this.mRouter.getRouteCount();
        for (int i = 0; i < routeCount; i++) {
            MediaRouter.RouteInfo routeAt = this.mRouter.getRouteAt(i);
            if (routeAt.matchesTypes(4) && routeAt.isSelected() && !routeAt.isConnecting()) {
                return this.mContext.getString(R$string.wifi_display_status_connected);
            }
        }
        return string;
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        this.mRouter.addCallback(4, this.mRouterCallback);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mRouter.removeCallback(this.mRouterCallback);
    }
}
