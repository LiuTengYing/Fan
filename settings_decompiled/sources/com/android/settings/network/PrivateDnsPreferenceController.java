package com.android.settings.network;

import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.ConnectivityManager;
import android.net.ConnectivitySettingsManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.util.ArrayUtils;
import com.android.settings.R$bool;
import com.android.settings.R$string;
import com.android.settings.core.BasePreferenceController;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
/* loaded from: classes.dex */
public class PrivateDnsPreferenceController extends BasePreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnStart, OnStop {
    private static final String KEY_PRIVATE_DNS_SETTINGS = "private_dns_settings";
    private static final Uri[] SETTINGS_URIS = {Settings.Global.getUriFor("private_dns_mode"), Settings.Global.getUriFor("private_dns_default_mode"), Settings.Global.getUriFor("private_dns_specifier")};
    private final ConnectivityManager mConnectivityManager;
    private final Handler mHandler;
    private LinkProperties mLatestLinkProperties;
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private Preference mPreference;
    private final ContentObserver mSettingsObserver;

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    @Override // com.android.settings.slices.Sliceable
    public /* bridge */ /* synthetic */ IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return KEY_PRIVATE_DNS_SETTINGS;
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

    public PrivateDnsPreferenceController(Context context) {
        super(context, KEY_PRIVATE_DNS_SETTINGS);
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.settings.network.PrivateDnsPreferenceController.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
                PrivateDnsPreferenceController.this.mLatestLinkProperties = linkProperties;
                if (PrivateDnsPreferenceController.this.mPreference != null) {
                    PrivateDnsPreferenceController privateDnsPreferenceController = PrivateDnsPreferenceController.this;
                    privateDnsPreferenceController.updateState(privateDnsPreferenceController.mPreference);
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                PrivateDnsPreferenceController.this.mLatestLinkProperties = null;
                if (PrivateDnsPreferenceController.this.mPreference != null) {
                    PrivateDnsPreferenceController privateDnsPreferenceController = PrivateDnsPreferenceController.this;
                    privateDnsPreferenceController.updateState(privateDnsPreferenceController.mPreference);
                }
            }
        };
        Handler handler = new Handler(Looper.getMainLooper());
        this.mHandler = handler;
        this.mSettingsObserver = new PrivateDnsSettingsObserver(handler);
        this.mConnectivityManager = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
    }

    @Override // com.android.settings.core.BasePreferenceController
    public int getAvailabilityStatus() {
        if (this.mContext.getResources().getBoolean(R$bool.config_show_private_dns_settings)) {
            return ((UserManager) this.mContext.getSystemService(UserManager.class)).isGuestUser() ? 4 : 0;
        }
        return 3;
    }

    @Override // com.android.settings.core.BasePreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = preferenceScreen.findPreference(getPreferenceKey());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        for (Uri uri : SETTINGS_URIS) {
            this.mContext.getContentResolver().registerContentObserver(uri, false, this.mSettingsObserver);
        }
        Network activeNetwork = this.mConnectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            this.mLatestLinkProperties = this.mConnectivityManager.getLinkProperties(activeNetwork);
        }
        this.mConnectivityManager.registerDefaultNetworkCallback(this.mNetworkCallback, this.mHandler);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        this.mContext.getContentResolver().unregisterContentObserver(this.mSettingsObserver);
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public CharSequence getSummary() {
        Resources resources = this.mContext.getResources();
        ContentResolver contentResolver = this.mContext.getContentResolver();
        int privateDnsMode = ConnectivitySettingsManager.getPrivateDnsMode(this.mContext);
        LinkProperties linkProperties = this.mLatestLinkProperties;
        boolean z = !ArrayUtils.isEmpty(linkProperties == null ? null : linkProperties.getValidatedPrivateDnsServers());
        if (privateDnsMode != 1) {
            if (privateDnsMode == 2) {
                if (z) {
                    return resources.getString(R$string.private_dns_mode_on);
                }
                return resources.getString(R$string.private_dns_mode_opportunistic);
            } else if (privateDnsMode != 3) {
                return "";
            } else {
                if (z) {
                    return PrivateDnsModeDialogPreference.getHostnameFromSettings(contentResolver);
                }
                return resources.getString(R$string.private_dns_mode_provider_failure);
            }
        }
        return resources.getString(R$string.private_dns_mode_off);
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        super.updateState(preference);
        preference.setEnabled(!isManagedByAdmin());
    }

    private boolean isManagedByAdmin() {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(this.mContext, "disallow_config_private_dns", UserHandle.myUserId()) != null;
    }

    /* loaded from: classes.dex */
    private class PrivateDnsSettingsObserver extends ContentObserver {
        public PrivateDnsSettingsObserver(Handler handler) {
            super(handler);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (PrivateDnsPreferenceController.this.mPreference != null) {
                PrivateDnsPreferenceController privateDnsPreferenceController = PrivateDnsPreferenceController.this;
                privateDnsPreferenceController.updateState(privateDnsPreferenceController.mPreference);
            }
        }
    }
}
