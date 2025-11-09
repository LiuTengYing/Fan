package com.android.settingslib.devicestate;

import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes2.dex */
public final class DeviceStateRotationLockSettingsManager {
    private static DeviceStateRotationLockSettingsManager sSingleton;
    private String[] mDeviceStateRotationLockDefaults;
    private SparseIntArray mDeviceStateRotationLockFallbackSettings;
    private SparseIntArray mDeviceStateRotationLockSettings;
    private String mLastSettingValue;
    private final SecureSettings mSecureSettings;
    private List<SettableDeviceState> mSettableDeviceStates;
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private final Set<DeviceStateRotationLockSettingsListener> mListeners = new HashSet();

    /* loaded from: classes2.dex */
    public interface DeviceStateRotationLockSettingsListener {
        void onSettingsChanged();
    }

    @VisibleForTesting
    DeviceStateRotationLockSettingsManager(Context context, SecureSettings secureSettings) {
        this.mSecureSettings = secureSettings;
        this.mDeviceStateRotationLockDefaults = context.getResources().getStringArray(17236105);
        loadDefaults();
        initializeInMemoryMap();
        listenForSettingsChange();
    }

    public static synchronized DeviceStateRotationLockSettingsManager getInstance(Context context) {
        DeviceStateRotationLockSettingsManager deviceStateRotationLockSettingsManager;
        synchronized (DeviceStateRotationLockSettingsManager.class) {
            if (sSingleton == null) {
                Context applicationContext = context.getApplicationContext();
                sSingleton = new DeviceStateRotationLockSettingsManager(applicationContext, new AndroidSecureSettings(applicationContext.getContentResolver()));
            }
            deviceStateRotationLockSettingsManager = sSingleton;
        }
        return deviceStateRotationLockSettingsManager;
    }

    @VisibleForTesting
    public static synchronized void resetInstance() {
        synchronized (DeviceStateRotationLockSettingsManager.class) {
            sSingleton = null;
        }
    }

    public static boolean isDeviceStateRotationLockEnabled(Context context) {
        return context.getResources().getStringArray(17236105).length > 0;
    }

    private void listenForSettingsChange() {
        this.mSecureSettings.registerContentObserver("device_state_rotation_lock", false, new ContentObserver(this.mMainHandler) { // from class: com.android.settingslib.devicestate.DeviceStateRotationLockSettingsManager.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                DeviceStateRotationLockSettingsManager.this.onPersistedSettingsChanged();
            }
        }, -2);
    }

    public void registerListener(DeviceStateRotationLockSettingsListener deviceStateRotationLockSettingsListener) {
        this.mListeners.add(deviceStateRotationLockSettingsListener);
    }

    public void unregisterListener(DeviceStateRotationLockSettingsListener deviceStateRotationLockSettingsListener) {
        if (this.mListeners.remove(deviceStateRotationLockSettingsListener)) {
            return;
        }
        Log.w("DSRotLockSettingsMngr", "Attempting to unregister a listener hadn't been registered");
    }

    public void updateSetting(int i, boolean z) {
        if (this.mDeviceStateRotationLockFallbackSettings.indexOfKey(i) >= 0) {
            i = this.mDeviceStateRotationLockFallbackSettings.get(i);
        }
        this.mDeviceStateRotationLockSettings.put(i, z ? 1 : 2);
        persistSettings();
    }

    public int getRotationLockSetting(int i) {
        int i2 = this.mDeviceStateRotationLockSettings.get(i, 0);
        return i2 == 0 ? getFallbackRotationLockSetting(i) : i2;
    }

    private int getFallbackRotationLockSetting(int i) {
        int indexOfKey = this.mDeviceStateRotationLockFallbackSettings.indexOfKey(i);
        if (indexOfKey < 0) {
            Log.w("DSRotLockSettingsMngr", "Setting is ignored, but no fallback was specified.");
            return 0;
        }
        return this.mDeviceStateRotationLockSettings.get(this.mDeviceStateRotationLockFallbackSettings.valueAt(indexOfKey), 0);
    }

    public boolean isRotationLocked(int i) {
        return getRotationLockSetting(i) == 1;
    }

    public boolean isRotationLockedForAllStates() {
        for (int i = 0; i < this.mDeviceStateRotationLockSettings.size(); i++) {
            if (this.mDeviceStateRotationLockSettings.valueAt(i) == 2) {
                return false;
            }
        }
        return true;
    }

    public List<SettableDeviceState> getSettableDeviceStates() {
        return new ArrayList(this.mSettableDeviceStates);
    }

    private void initializeInMemoryMap() {
        String stringForUser = this.mSecureSettings.getStringForUser("device_state_rotation_lock", -2);
        if (TextUtils.isEmpty(stringForUser)) {
            fallbackOnDefaults();
            return;
        }
        String[] split = stringForUser.split(":");
        if (split.length % 2 != 0) {
            Log.wtf("DSRotLockSettingsMngr", "Can't deserialize saved settings, falling back on defaults");
            fallbackOnDefaults();
            return;
        }
        this.mDeviceStateRotationLockSettings = new SparseIntArray(split.length / 2);
        int i = 0;
        while (i < split.length - 1) {
            int i2 = i + 1;
            try {
                int i3 = i2 + 1;
                this.mDeviceStateRotationLockSettings.put(Integer.parseInt(split[i]), Integer.parseInt(split[i2]));
                i = i3;
            } catch (NumberFormatException e) {
                Log.wtf("DSRotLockSettingsMngr", "Error deserializing one of the saved settings", e);
                fallbackOnDefaults();
                return;
            }
        }
    }

    @VisibleForTesting
    public void resetStateForTesting(Resources resources) {
        this.mDeviceStateRotationLockDefaults = resources.getStringArray(17236105);
        fallbackOnDefaults();
    }

    private void fallbackOnDefaults() {
        loadDefaults();
        persistSettings();
    }

    private void persistSettings() {
        if (this.mDeviceStateRotationLockSettings.size() == 0) {
            persistSettingIfChanged("");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(this.mDeviceStateRotationLockSettings.keyAt(0));
        sb.append(":");
        sb.append(this.mDeviceStateRotationLockSettings.valueAt(0));
        for (int i = 1; i < this.mDeviceStateRotationLockSettings.size(); i++) {
            sb.append(":");
            sb.append(this.mDeviceStateRotationLockSettings.keyAt(i));
            sb.append(":");
            sb.append(this.mDeviceStateRotationLockSettings.valueAt(i));
        }
        persistSettingIfChanged(sb.toString());
    }

    private void persistSettingIfChanged(String str) {
        if (TextUtils.equals(this.mLastSettingValue, str)) {
            return;
        }
        this.mLastSettingValue = str;
        this.mSecureSettings.putStringForUser("device_state_rotation_lock", str, -2);
    }

    private void loadDefaults() {
        String[] strArr;
        this.mSettableDeviceStates = new ArrayList(this.mDeviceStateRotationLockDefaults.length);
        this.mDeviceStateRotationLockSettings = new SparseIntArray(this.mDeviceStateRotationLockDefaults.length);
        this.mDeviceStateRotationLockFallbackSettings = new SparseIntArray(1);
        for (String str : this.mDeviceStateRotationLockDefaults) {
            String[] split = str.split(":");
            try {
                int parseInt = Integer.parseInt(split[0]);
                int parseInt2 = Integer.parseInt(split[1]);
                if (parseInt2 == 0) {
                    if (split.length == 3) {
                        this.mDeviceStateRotationLockFallbackSettings.put(parseInt, Integer.parseInt(split[2]));
                    } else {
                        Log.w("DSRotLockSettingsMngr", "Rotation lock setting is IGNORED, but values have unexpected size of " + split.length);
                    }
                }
                this.mSettableDeviceStates.add(new SettableDeviceState(parseInt, parseInt2 != 0));
                this.mDeviceStateRotationLockSettings.put(parseInt, parseInt2);
            } catch (NumberFormatException e) {
                Log.wtf("DSRotLockSettingsMngr", "Error parsing settings entry. Entry was: " + str, e);
                return;
            }
        }
    }

    @VisibleForTesting
    public void onPersistedSettingsChanged() {
        initializeInMemoryMap();
        notifyListeners();
    }

    private void notifyListeners() {
        for (DeviceStateRotationLockSettingsListener deviceStateRotationLockSettingsListener : this.mListeners) {
            deviceStateRotationLockSettingsListener.onSettingsChanged();
        }
    }

    /* loaded from: classes2.dex */
    public static class SettableDeviceState {
        private final int mDeviceState;
        private final boolean mIsSettable;

        SettableDeviceState(int i, boolean z) {
            this.mDeviceState = i;
            this.mIsSettable = z;
        }

        public int getDeviceState() {
            return this.mDeviceState;
        }

        public boolean isSettable() {
            return this.mIsSettable;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof SettableDeviceState) {
                SettableDeviceState settableDeviceState = (SettableDeviceState) obj;
                return this.mDeviceState == settableDeviceState.mDeviceState && this.mIsSettable == settableDeviceState.mIsSettable;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mDeviceState), Boolean.valueOf(this.mIsSettable));
        }
    }
}
