package com.android.settings.network;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.TetheringManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.FeatureFlagUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.R$string;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.TetherUtil;
import com.android.settingslib.Utils;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnCreate;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.util.concurrent.atomic.AtomicReference;
/* loaded from: classes.dex */
public class TetherPreferenceController extends AbstractPreferenceController implements PreferenceControllerMixin, LifecycleObserver, OnCreate, OnResume, OnPause, OnDestroy {
    private final boolean mAdminDisallowedTetherConfig;
    private SettingObserver mAirplaneModeObserver;
    private final BluetoothAdapter mBluetoothAdapter;
    private final AtomicReference<BluetoothPan> mBluetoothPan;
    final BluetoothProfile.ServiceListener mBtProfileServiceListener;
    private Preference mPreference;
    private TetherBroadcastReceiver mTetherReceiver;
    private final TetheringManager mTetheringManager;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "tether_settings";
    }

    TetherPreferenceController() {
        super(null);
        this.mBtProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.network.TetherPreferenceController.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                TetherPreferenceController.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
                TetherPreferenceController.this.updateSummary();
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                TetherPreferenceController.this.mBluetoothPan.set(null);
            }
        };
        this.mAdminDisallowedTetherConfig = false;
        this.mBluetoothPan = new AtomicReference<>();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mTetheringManager = null;
    }

    public TetherPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        this.mBtProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: com.android.settings.network.TetherPreferenceController.1
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                TetherPreferenceController.this.mBluetoothPan.set((BluetoothPan) bluetoothProfile);
                TetherPreferenceController.this.updateSummary();
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                TetherPreferenceController.this.mBluetoothPan.set(null);
            }
        };
        this.mBluetoothPan = new AtomicReference<>();
        this.mAdminDisallowedTetherConfig = isTetherConfigDisallowed(context);
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mTetheringManager = (TetheringManager) context.getSystemService(TetheringManager.class);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        Preference findPreference = preferenceScreen.findPreference("tether_settings");
        this.mPreference = findPreference;
        if (findPreference == null || this.mAdminDisallowedTetherConfig) {
            return;
        }
        findPreference.setTitle(Utils.getTetheringLabel(this.mTetheringManager));
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public boolean isAvailable() {
        return TetherUtil.isTetherAvailable(this.mContext) && !FeatureFlagUtils.isEnabled(this.mContext, "settings_tether_all_in_one");
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        updateSummary();
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnCreate
    public void onCreate(Bundle bundle) {
        BluetoothAdapter bluetoothAdapter = this.mBluetoothAdapter;
        if (bluetoothAdapter == null || bluetoothAdapter.getState() != 12) {
            return;
        }
        this.mBluetoothAdapter.getProfileProxy(this.mContext, this.mBtProfileServiceListener, 5);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mAirplaneModeObserver == null) {
            this.mAirplaneModeObserver = new SettingObserver();
        }
        if (this.mTetherReceiver == null) {
            this.mTetherReceiver = new TetherBroadcastReceiver();
        }
        this.mContext.registerReceiver(this.mTetherReceiver, new IntentFilter("android.net.conn.TETHER_STATE_CHANGED"));
        ContentResolver contentResolver = this.mContext.getContentResolver();
        SettingObserver settingObserver = this.mAirplaneModeObserver;
        contentResolver.registerContentObserver(settingObserver.uri, false, settingObserver);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        if (this.mAirplaneModeObserver != null) {
            this.mContext.getContentResolver().unregisterContentObserver(this.mAirplaneModeObserver);
        }
        TetherBroadcastReceiver tetherBroadcastReceiver = this.mTetherReceiver;
        if (tetherBroadcastReceiver != null) {
            this.mContext.unregisterReceiver(tetherBroadcastReceiver);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        BluetoothAdapter bluetoothAdapter;
        BluetoothProfile andSet = this.mBluetoothPan.getAndSet(null);
        if (andSet == null || (bluetoothAdapter = this.mBluetoothAdapter) == null) {
            return;
        }
        bluetoothAdapter.closeProfileProxy(5, andSet);
    }

    public static boolean isTetherConfigDisallowed(Context context) {
        return RestrictedLockUtilsInternal.checkIfRestrictionEnforced(context, "no_config_tethering", UserHandle.myUserId()) != null;
    }

    void updateSummary() {
        boolean z;
        boolean z2;
        BluetoothAdapter bluetoothAdapter;
        if (this.mPreference == null) {
            return;
        }
        String[] tetheredIfaces = this.mTetheringManager.getTetheredIfaces();
        String[] tetherableWifiRegexs = this.mTetheringManager.getTetherableWifiRegexs();
        String[] tetherableBluetoothRegexs = this.mTetheringManager.getTetherableBluetoothRegexs();
        boolean z3 = false;
        if (tetheredIfaces != null) {
            if (tetherableWifiRegexs != null) {
                z2 = false;
                for (String str : tetheredIfaces) {
                    int length = tetherableWifiRegexs.length;
                    int i = 0;
                    while (true) {
                        if (i >= length) {
                            break;
                        } else if (str.matches(tetherableWifiRegexs[i])) {
                            z2 = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                }
            } else {
                z2 = false;
            }
            if (tetheredIfaces.length > 1) {
                z = true;
            } else {
                z = tetheredIfaces.length == 1 ? !z2 : false;
            }
        } else {
            z = false;
            z2 = false;
        }
        if (!z && tetherableBluetoothRegexs != null && tetherableBluetoothRegexs.length > 0 && (bluetoothAdapter = this.mBluetoothAdapter) != null && bluetoothAdapter.getState() == 12) {
            BluetoothPan bluetoothPan = this.mBluetoothPan.get();
            if (bluetoothPan != null && bluetoothPan.isTetheringOn()) {
                z3 = true;
            }
            z = z3;
        }
        if (!z2 && !z) {
            this.mPreference.setSummary(R$string.switch_off_text);
        } else if (z2 && z) {
            this.mPreference.setSummary(R$string.tether_settings_summary_hotspot_on_tether_on);
        } else if (z2) {
            this.mPreference.setSummary(R$string.tether_settings_summary_hotspot_on_tether_off);
        } else {
            this.mPreference.setSummary(R$string.tether_settings_summary_hotspot_off_tether_on);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateSummaryToOff() {
        Preference preference = this.mPreference;
        if (preference == null) {
            return;
        }
        preference.setSummary(R$string.switch_off_text);
    }

    /* loaded from: classes.dex */
    class SettingObserver extends ContentObserver {
        public final Uri uri;

        public SettingObserver() {
            super(new Handler());
            this.uri = Settings.Global.getUriFor("airplane_mode_on");
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
            if (this.uri.equals(uri)) {
                if (Settings.Global.getInt(((AbstractPreferenceController) TetherPreferenceController.this).mContext.getContentResolver(), "airplane_mode_on", 0) != 0) {
                    TetherPreferenceController.this.updateSummaryToOff();
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class TetherBroadcastReceiver extends BroadcastReceiver {
        TetherBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            TetherPreferenceController.this.updateSummary();
        }
    }
}
