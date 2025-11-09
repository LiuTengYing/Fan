package com.android.settings.network;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.VpnManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.security.LegacyVpnProfileStore;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settings.vpn2.VpnInfoPreference;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public class VpnPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnResume, OnPause {
    private static final NetworkRequest REQUEST = new NetworkRequest.Builder().removeCapability(15).removeCapability(13).removeCapability(14).build();
    private ConnectivityManager mConnectivityManager;
    private final ConnectivityManager.NetworkCallback mNetworkCallback;
    private Preference mPreference;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "vpn_settings";
    }

    public VpnPreferenceController(Context context) {
        super(context);
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.settings.network.VpnPreferenceController.1
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                VpnPreferenceController.this.updateSummary();
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                VpnPreferenceController.this.updateSummary();
            }
        };
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = getEffectivePreference(preferenceScreen);
    }

    protected Preference getEffectivePreference(PreferenceScreen preferenceScreen) {
        Preference findPreference = preferenceScreen.findPreference("vpn_settings");
        if (findPreference == null) {
            return null;
        }
        String string = Settings.Global.getString(this.mContext.getContentResolver(), "airplane_mode_toggleable_radios");
        if (string == null || !string.contains("wifi")) {
            findPreference.setDependency("airplane_mode");
        }
        return findPreference;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return !RestrictedLockUtilsInternal.hasBaseUserRestriction(this.mContext, "no_config_vpn", UserHandle.myUserId());
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        ConnectivityManager connectivityManager = this.mConnectivityManager;
        if (connectivityManager != null) {
            connectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
            this.mConnectivityManager = null;
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (isAvailable()) {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.mContext.getSystemService(ConnectivityManager.class);
            this.mConnectivityManager = connectivityManager;
            connectivityManager.registerNetworkCallback(REQUEST, this.mNetworkCallback);
            return;
        }
        this.mConnectivityManager = null;
    }

    void updateSummary() {
        int i;
        LegacyVpnInfo legacyVpnInfo;
        if (this.mPreference == null) {
            return;
        }
        UserManager userManager = (UserManager) this.mContext.getSystemService(UserManager.class);
        VpnManager vpnManager = (VpnManager) this.mContext.getSystemService(VpnManager.class);
        final String insecureVpnSummaryOverride = getInsecureVpnSummaryOverride(userManager, vpnManager);
        if (insecureVpnSummaryOverride == null) {
            UserInfo userInfo = userManager.getUserInfo(UserHandle.myUserId());
            if (userInfo.isRestricted()) {
                i = userInfo.restrictedProfileParentId;
            } else {
                i = userInfo.id;
            }
            VpnConfig vpnConfig = vpnManager.getVpnConfig(i);
            if (vpnConfig != null && vpnConfig.legacy && ((legacyVpnInfo = vpnManager.getLegacyVpnInfo(i)) == null || legacyVpnInfo.state != 3)) {
                vpnConfig = null;
            }
            if (vpnConfig == null) {
                insecureVpnSummaryOverride = this.mContext.getString(R$string.vpn_disconnected_summary);
            } else {
                insecureVpnSummaryOverride = getNameForVpnConfig(vpnConfig, UserHandle.of(i));
            }
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settings.network.VpnPreferenceController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                VpnPreferenceController.this.lambda$updateSummary$0(insecureVpnSummaryOverride);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateSummary$0(String str) {
        this.mPreference.setSummary(str);
    }

    protected int getNumberOfNonLegacyVpn(UserManager userManager, final VpnManager vpnManager) {
        return (int) userManager.getUsers().stream().map(new Function() { // from class: com.android.settings.network.VpnPreferenceController$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                VpnConfig lambda$getNumberOfNonLegacyVpn$1;
                lambda$getNumberOfNonLegacyVpn$1 = VpnPreferenceController.lambda$getNumberOfNonLegacyVpn$1(vpnManager, (UserInfo) obj);
                return lambda$getNumberOfNonLegacyVpn$1;
            }
        }).filter(new Predicate() { // from class: com.android.settings.network.VpnPreferenceController$$ExternalSyntheticLambda4
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getNumberOfNonLegacyVpn$2;
                lambda$getNumberOfNonLegacyVpn$2 = VpnPreferenceController.lambda$getNumberOfNonLegacyVpn$2((VpnConfig) obj);
                return lambda$getNumberOfNonLegacyVpn$2;
            }
        }).count();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ VpnConfig lambda$getNumberOfNonLegacyVpn$1(VpnManager vpnManager, UserInfo userInfo) {
        return vpnManager.getVpnConfig(userInfo.id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getNumberOfNonLegacyVpn$2(VpnConfig vpnConfig) {
        return (vpnConfig == null || vpnConfig.legacy) ? false : true;
    }

    protected String getInsecureVpnSummaryOverride(UserManager userManager, VpnManager vpnManager) {
        if (this.mPreference instanceof VpnInfoPreference) {
            String[] list = LegacyVpnProfileStore.list("VPN_");
            int insecureVpnCount = getInsecureVpnCount(list);
            boolean z = insecureVpnCount > 0;
            ((VpnInfoPreference) this.mPreference).setInsecureVpn(z);
            if (z) {
                int length = list.length;
                if (length > 1 || length + getNumberOfNonLegacyVpn(userManager, vpnManager) != 1) {
                    if (insecureVpnCount == 1) {
                        return this.mContext.getString(R$string.vpn_settings_single_insecure_multiple_total, Integer.valueOf(insecureVpnCount));
                    }
                    return this.mContext.getString(R$string.vpn_settings_multiple_insecure_multiple_total, Integer.valueOf(insecureVpnCount));
                }
                return this.mContext.getString(R$string.vpn_settings_insecure_single);
            }
            return null;
        }
        return null;
    }

    String getNameForVpnConfig(VpnConfig vpnConfig, UserHandle userHandle) {
        if (vpnConfig.legacy) {
            return this.mContext.getString(R$string.wifi_display_status_connected);
        }
        String str = vpnConfig.user;
        try {
            Context context = this.mContext;
            return VpnConfig.getVpnLabel(context.createPackageContextAsUser(context.getPackageName(), 0, userHandle), str).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VpnPreferenceController", "Package " + str + " is not present", e);
            return null;
        }
    }

    protected int getInsecureVpnCount(String[] strArr) {
        return (int) Arrays.stream(strArr).map(new Function() { // from class: com.android.settings.network.VpnPreferenceController$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                VpnProfile lambda$getInsecureVpnCount$3;
                lambda$getInsecureVpnCount$3 = VpnPreferenceController.lambda$getInsecureVpnCount$3((String) obj);
                return lambda$getInsecureVpnCount$3;
            }
        }).filter(new Predicate() { // from class: com.android.settings.network.VpnPreferenceController$$ExternalSyntheticLambda2
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$getInsecureVpnCount$4;
                lambda$getInsecureVpnCount$4 = VpnPreferenceController.lambda$getInsecureVpnCount$4((VpnProfile) obj);
                return lambda$getInsecureVpnCount$4;
            }
        }).count();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ VpnProfile lambda$getInsecureVpnCount$3(String str) {
        return VpnProfile.decode(str, LegacyVpnProfileStore.get("VPN_" + str));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$getInsecureVpnCount$4(VpnProfile vpnProfile) {
        return VpnProfile.isLegacyType(vpnProfile.type);
    }
}
