package com.android.settings.vpn2;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.VpnManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.LegacyVpnProfileStore;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.settings.R$id;
import com.android.settings.R$menu;
import com.android.settings.R$string;
import com.android.settings.R$xml;
import com.android.settings.RestrictedSettingsFragment;
import com.android.settings.widget.GearPreference;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.google.android.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
/* loaded from: classes.dex */
public class VpnSettings extends RestrictedSettingsFragment implements Handler.Callback, Preference.OnPreferenceClickListener {
    private static final NetworkRequest VPN_REQUEST = new NetworkRequest.Builder().removeCapability(15).removeCapability(13).removeCapability(14).build();
    private Map<AppVpnInfo, AppPreference> mAppPreferences;
    private LegacyVpnInfo mConnectedLegacyVpn;
    private ConnectivityManager mConnectivityManager;
    private GearPreference.OnGearClickListener mGearListener;
    private Map<String, LegacyVpnPreference> mLegacyVpnPreferences;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private boolean mUnavailable;
    @GuardedBy({"this"})
    private Handler mUpdater;
    private HandlerThread mUpdaterThread;
    private UserManager mUserManager;
    private VpnManager mVpnManager;

    @Override // com.android.settingslib.core.instrumentation.Instrumentable
    public int getMetricsCategory() {
        return 100;
    }

    public VpnSettings() {
        super("no_config_vpn");
        this.mLegacyVpnPreferences = new ArrayMap();
        this.mAppPreferences = new ArrayMap();
        this.mGearListener = new GearPreference.OnGearClickListener() { // from class: com.android.settings.vpn2.VpnSettings.1
            @Override // com.android.settings.widget.GearPreference.OnGearClickListener
            public void onGearClick(GearPreference gearPreference) {
                if (gearPreference instanceof LegacyVpnPreference) {
                    ConfigDialogFragment.show(VpnSettings.this, ((LegacyVpnPreference) gearPreference).getProfile(), true, true);
                } else if (gearPreference instanceof AppPreference) {
                    AppManagementFragment.show(VpnSettings.this.getPrefContext(), (AppPreference) gearPreference, VpnSettings.this.getMetricsCategory());
                }
            }
        };
        this.mNetworkCallback = new ConnectivityManager.NetworkCallback() { // from class: com.android.settings.vpn2.VpnSettings.2
            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onAvailable(Network network) {
                if (VpnSettings.this.mUpdater != null) {
                    VpnSettings.this.mUpdater.sendEmptyMessage(0);
                }
            }

            @Override // android.net.ConnectivityManager.NetworkCallback
            public void onLost(Network network) {
                if (VpnSettings.this.mUpdater != null) {
                    VpnSettings.this.mUpdater.sendEmptyMessage(0);
                }
            }
        };
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, androidx.fragment.app.Fragment
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
        this.mUserManager = (UserManager) getSystemService("user");
        this.mConnectivityManager = (ConnectivityManager) getSystemService("connectivity");
        this.mVpnManager = (VpnManager) getSystemService("vpn_management");
        boolean isUiRestricted = isUiRestricted();
        this.mUnavailable = isUiRestricted;
        setHasOptionsMenu(!isUiRestricted);
        addPreferencesFromResource(R$xml.vpn_settings2);
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        if (!getContext().getPackageManager().hasSystemFeature("android.software.ipsec_tunnels")) {
            Log.wtf("VpnSettings", "FEATURE_IPSEC_TUNNELS missing from system, cannot create new VPNs");
        } else {
            menuInflater.inflate(R$menu.vpn, menu);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        for (int i = 0; i < menu.size(); i++) {
            if (isUiRestrictedByOnlyAdmin()) {
                RestrictedLockUtilsInternal.setMenuItemAsDisabledByAdmin(getPrefContext(), menu.getItem(i), getRestrictionEnforcedAdmin());
            } else {
                menu.getItem(i).setEnabled(!this.mUnavailable);
            }
        }
    }

    @Override // com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R$id.vpn_create) {
            long currentTimeMillis = System.currentTimeMillis();
            while (this.mLegacyVpnPreferences.containsKey(Long.toHexString(currentTimeMillis))) {
                currentTimeMillis++;
            }
            ConfigDialogFragment.show(this, new VpnProfile(Long.toHexString(currentTimeMillis)), true, false);
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.android.settings.RestrictedSettingsFragment, com.android.settings.SettingsPreferenceFragment, com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        boolean hasUserRestriction = this.mUserManager.hasUserRestriction("no_config_vpn");
        this.mUnavailable = hasUserRestriction;
        if (hasUserRestriction) {
            if (!isUiRestrictedByOnlyAdmin()) {
                getEmptyTextView().setText(R$string.vpn_settings_not_available);
            }
            getPreferenceScreen().removeAll();
            return;
        }
        setEmptyView(getEmptyTextView());
        getEmptyTextView().setText(R$string.vpn_no_vpns_added);
        this.mConnectivityManager.registerNetworkCallback(VPN_REQUEST, this.mNetworkCallback);
        HandlerThread handlerThread = new HandlerThread("Refresh VPN list in background");
        this.mUpdaterThread = handlerThread;
        handlerThread.start();
        Handler handler = new Handler(this.mUpdaterThread.getLooper(), this);
        this.mUpdater = handler;
        handler.sendEmptyMessage(0);
    }

    @Override // com.android.settings.core.InstrumentedPreferenceFragment, com.android.settingslib.core.lifecycle.ObservablePreferenceFragment, androidx.fragment.app.Fragment
    public void onPause() {
        if (this.mUnavailable) {
            super.onPause();
            return;
        }
        this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
        synchronized (this) {
            this.mUpdater.removeCallbacksAndMessages(null);
            this.mUpdater = null;
            this.mUpdaterThread.quit();
            this.mUpdaterThread = null;
        }
        super.onPause();
    }

    @Override // android.os.Handler.Callback
    public boolean handleMessage(Message message) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return true;
        }
        Context applicationContext = activity.getApplicationContext();
        List<VpnProfile> loadVpnProfiles = loadVpnProfiles();
        List<AppVpnInfo> vpnApps = getVpnApps(applicationContext, true);
        Map<String, LegacyVpnInfo> connectedLegacyVpns = getConnectedLegacyVpns();
        activity.runOnUiThread(new UpdatePreferences(this).legacyVpns(loadVpnProfiles, connectedLegacyVpns, VpnUtils.getLockdownVpn()).appVpns(vpnApps, getConnectedAppVpns(), getAlwaysOnAppVpnInfos()));
        synchronized (this) {
            Handler handler = this.mUpdater;
            if (handler != null) {
                handler.removeMessages(0);
                this.mUpdater.sendEmptyMessageDelayed(0, 1000L);
            }
        }
        return true;
    }

    /* loaded from: classes.dex */
    static class UpdatePreferences implements Runnable {
        private final VpnSettings mSettings;
        private List<VpnProfile> vpnProfiles = Collections.emptyList();
        private List<AppVpnInfo> vpnApps = Collections.emptyList();
        private Map<String, LegacyVpnInfo> connectedLegacyVpns = Collections.emptyMap();
        private Set<AppVpnInfo> connectedAppVpns = Collections.emptySet();
        private Set<AppVpnInfo> alwaysOnAppVpnInfos = Collections.emptySet();
        private String lockdownVpnKey = null;

        public UpdatePreferences(VpnSettings vpnSettings) {
            this.mSettings = vpnSettings;
        }

        public final UpdatePreferences legacyVpns(List<VpnProfile> list, Map<String, LegacyVpnInfo> map, String str) {
            this.vpnProfiles = list;
            this.connectedLegacyVpns = map;
            this.lockdownVpnKey = str;
            return this;
        }

        public final UpdatePreferences appVpns(List<AppVpnInfo> list, Set<AppVpnInfo> set, Set<AppVpnInfo> set2) {
            this.vpnApps = list;
            this.connectedAppVpns = set;
            this.alwaysOnAppVpnInfos = set2;
            return this;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mSettings.canAddPreferences()) {
                ArraySet arraySet = new ArraySet();
                Iterator<VpnProfile> it = this.vpnProfiles.iterator();
                while (true) {
                    boolean z = false;
                    if (!it.hasNext()) {
                        break;
                    }
                    VpnProfile next = it.next();
                    LegacyVpnPreference findOrCreatePreference = this.mSettings.findOrCreatePreference(next, true);
                    if (this.connectedLegacyVpns.containsKey(next.key)) {
                        findOrCreatePreference.setState(this.connectedLegacyVpns.get(next.key).state);
                    } else {
                        findOrCreatePreference.setState(ManageablePreference.STATE_NONE);
                    }
                    String str = this.lockdownVpnKey;
                    if (str != null && str.equals(next.key)) {
                        z = true;
                    }
                    findOrCreatePreference.setAlwaysOn(z);
                    findOrCreatePreference.setInsecureVpn(VpnProfile.isLegacyType(next.type));
                    arraySet.add(findOrCreatePreference);
                }
                for (LegacyVpnInfo legacyVpnInfo : this.connectedLegacyVpns.values()) {
                    LegacyVpnPreference findOrCreatePreference2 = this.mSettings.findOrCreatePreference(new VpnProfile(legacyVpnInfo.key), false);
                    findOrCreatePreference2.setState(legacyVpnInfo.state);
                    String str2 = this.lockdownVpnKey;
                    findOrCreatePreference2.setAlwaysOn(str2 != null && str2.equals(legacyVpnInfo.key));
                    arraySet.add(findOrCreatePreference2);
                }
                for (AppVpnInfo appVpnInfo : this.vpnApps) {
                    AppPreference findOrCreatePreference3 = this.mSettings.findOrCreatePreference(appVpnInfo);
                    if (this.connectedAppVpns.contains(appVpnInfo)) {
                        findOrCreatePreference3.setState(3);
                    } else {
                        findOrCreatePreference3.setState(AppPreference.STATE_DISCONNECTED);
                    }
                    findOrCreatePreference3.setAlwaysOn(this.alwaysOnAppVpnInfos.contains(appVpnInfo));
                    arraySet.add(findOrCreatePreference3);
                }
                this.mSettings.setShownPreferences(arraySet);
            }
        }
    }

    public boolean canAddPreferences() {
        return isAdded();
    }

    public void setShownPreferences(Collection<Preference> collection) {
        this.mLegacyVpnPreferences.values().retainAll(collection);
        this.mAppPreferences.values().retainAll(collection);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (int preferenceCount = preferenceScreen.getPreferenceCount() - 1; preferenceCount >= 0; preferenceCount--) {
            Preference preference = preferenceScreen.getPreference(preferenceCount);
            if (collection.contains(preference)) {
                collection.remove(preference);
            } else {
                preferenceScreen.removePreference(preference);
            }
        }
        for (Preference preference2 : collection) {
            preferenceScreen.addPreference(preference2);
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (preference instanceof LegacyVpnPreference) {
            VpnProfile profile = ((LegacyVpnPreference) preference).getProfile();
            LegacyVpnInfo legacyVpnInfo = this.mConnectedLegacyVpn;
            if (legacyVpnInfo != null && profile.key.equals(legacyVpnInfo.key)) {
                LegacyVpnInfo legacyVpnInfo2 = this.mConnectedLegacyVpn;
                if (legacyVpnInfo2.state == 3) {
                    try {
                        legacyVpnInfo2.intent.send();
                        return true;
                    } catch (Exception e) {
                        Log.w("VpnSettings", "Starting config intent failed", e);
                    }
                }
            }
            ConfigDialogFragment.show(this, profile, false, true);
            return true;
        } else if (preference instanceof AppPreference) {
            AppPreference appPreference = (AppPreference) preference;
            boolean z = appPreference.getState() == 3;
            if (!z) {
                try {
                    UserHandle of = UserHandle.of(appPreference.getUserId());
                    Context createPackageContextAsUser = getActivity().createPackageContextAsUser(getActivity().getPackageName(), 0, of);
                    Intent launchIntentForPackage = createPackageContextAsUser.getPackageManager().getLaunchIntentForPackage(appPreference.getPackageName());
                    if (launchIntentForPackage != null) {
                        createPackageContextAsUser.startActivityAsUser(launchIntentForPackage, of);
                        return true;
                    }
                } catch (PackageManager.NameNotFoundException e2) {
                    Log.w("VpnSettings", "VPN provider does not exist: " + appPreference.getPackageName(), e2);
                }
            }
            AppDialogFragment.show(this, appPreference.getPackageInfo(), appPreference.getLabel(), false, z);
            return true;
        } else {
            return false;
        }
    }

    @Override // com.android.settings.support.actionbar.HelpResourceProvider
    public int getHelpResource() {
        return R$string.help_url_vpn;
    }

    public LegacyVpnPreference findOrCreatePreference(VpnProfile vpnProfile, boolean z) {
        boolean z2;
        LegacyVpnPreference legacyVpnPreference = this.mLegacyVpnPreferences.get(vpnProfile.key);
        if (legacyVpnPreference == null) {
            legacyVpnPreference = new LegacyVpnPreference(getPrefContext());
            legacyVpnPreference.setOnGearClickListener(this.mGearListener);
            legacyVpnPreference.setOnPreferenceClickListener(this);
            this.mLegacyVpnPreferences.put(vpnProfile.key, legacyVpnPreference);
            z2 = true;
        } else {
            z2 = false;
        }
        if (z2 || z) {
            legacyVpnPreference.setProfile(vpnProfile);
        }
        return legacyVpnPreference;
    }

    public AppPreference findOrCreatePreference(AppVpnInfo appVpnInfo) {
        AppPreference appPreference = this.mAppPreferences.get(appVpnInfo);
        if (appPreference == null) {
            AppPreference appPreference2 = new AppPreference(getPrefContext(), appVpnInfo.userId, appVpnInfo.packageName);
            appPreference2.setOnGearClickListener(this.mGearListener);
            appPreference2.setOnPreferenceClickListener(this);
            this.mAppPreferences.put(appVpnInfo, appPreference2);
            return appPreference2;
        }
        return appPreference;
    }

    private Map<String, LegacyVpnInfo> getConnectedLegacyVpns() {
        LegacyVpnInfo legacyVpnInfo = this.mVpnManager.getLegacyVpnInfo(UserHandle.myUserId());
        this.mConnectedLegacyVpn = legacyVpnInfo;
        if (legacyVpnInfo != null) {
            return Collections.singletonMap(legacyVpnInfo.key, legacyVpnInfo);
        }
        return Collections.emptyMap();
    }

    private Set<AppVpnInfo> getConnectedAppVpns() {
        ArraySet arraySet = new ArraySet();
        for (UserHandle userHandle : this.mUserManager.getUserProfiles()) {
            VpnConfig vpnConfig = this.mVpnManager.getVpnConfig(userHandle.getIdentifier());
            if (vpnConfig != null && !vpnConfig.legacy) {
                arraySet.add(new AppVpnInfo(userHandle.getIdentifier(), vpnConfig.user));
            }
        }
        return arraySet;
    }

    private Set<AppVpnInfo> getAlwaysOnAppVpnInfos() {
        ArraySet arraySet = new ArraySet();
        for (UserHandle userHandle : this.mUserManager.getUserProfiles()) {
            int identifier = userHandle.getIdentifier();
            String alwaysOnVpnPackageForUser = this.mVpnManager.getAlwaysOnVpnPackageForUser(identifier);
            if (alwaysOnVpnPackageForUser != null) {
                arraySet.add(new AppVpnInfo(identifier, alwaysOnVpnPackageForUser));
            }
        }
        return arraySet;
    }

    static List<AppVpnInfo> getVpnApps(Context context, boolean z) {
        Set singleton;
        ArrayList newArrayList = Lists.newArrayList();
        if (z) {
            singleton = new ArraySet();
            for (UserHandle userHandle : UserManager.get(context).getUserProfiles()) {
                singleton.add(Integer.valueOf(userHandle.getIdentifier()));
            }
        } else {
            singleton = Collections.singleton(Integer.valueOf(UserHandle.myUserId()));
        }
        List<AppOpsManager.PackageOps> packagesForOps = ((AppOpsManager) context.getSystemService("appops")).getPackagesForOps(new int[]{47, 94});
        if (packagesForOps != null) {
            for (AppOpsManager.PackageOps packageOps : packagesForOps) {
                int userId = UserHandle.getUserId(packageOps.getUid());
                if (singleton.contains(Integer.valueOf(userId))) {
                    boolean z2 = false;
                    for (AppOpsManager.OpEntry opEntry : packageOps.getOps()) {
                        if (opEntry.getOp() == 47 || opEntry.getOp() == 94) {
                            if (opEntry.getMode() == 0) {
                                z2 = true;
                            }
                        }
                    }
                    if (z2) {
                        newArrayList.add(new AppVpnInfo(userId, packageOps.getPackageName()));
                    }
                }
            }
        }
        Collections.sort(newArrayList);
        return newArrayList;
    }

    private static List<VpnProfile> loadVpnProfiles() {
        String[] list;
        ArrayList newArrayList = Lists.newArrayList();
        for (String str : LegacyVpnProfileStore.list("VPN_")) {
            VpnProfile decode = VpnProfile.decode(str, LegacyVpnProfileStore.get("VPN_" + str));
            if (decode != null) {
                newArrayList.add(decode);
            }
        }
        return newArrayList;
    }
}
