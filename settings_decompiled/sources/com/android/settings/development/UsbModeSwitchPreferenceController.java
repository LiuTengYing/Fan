package com.android.settings.development;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.development.DeveloperOptionsPreferenceController;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
/* loaded from: classes.dex */
public class UsbModeSwitchPreferenceController extends DeveloperOptionsPreferenceController implements Preference.OnPreferenceChangeListener, LifecycleObserver, OnResume, OnPause, PreferenceControllerMixin {
    static final int SETTING_VALUE_OFF = 0;
    private String USB_MODE_PATH;
    private RestrictedSwitchPreference mPreference;
    SettingsObserver mSettingsObserver;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "usb_mode";
    }

    public UsbModeSwitchPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        this.USB_MODE_PATH = "/sys/devices/platform/extcon-virt/host_dev";
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController, com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedSwitchPreference) preferenceScreen.findPreference(getPreferenceKey());
        String mode = getMode();
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (restrictedSwitchPreference != null) {
            restrictedSwitchPreference.setSummary(mode);
            this.mPreference.setChecked(mode.contains("device"));
        }
    }

    @Override // androidx.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object obj) {
        boolean booleanValue = ((Boolean) obj).booleanValue();
        Log.d("fangli", "setMode stayAwake ===" + booleanValue);
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (restrictedSwitchPreference != null) {
            restrictedSwitchPreference.setSummary(booleanValue ? "device" : "host");
        }
        if (booleanValue) {
            setMode("devices");
            return true;
        }
        setMode("host");
        return true;
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void updateState(Preference preference) {
        String mode = getMode();
        RestrictedSwitchPreference restrictedSwitchPreference = this.mPreference;
        if (restrictedSwitchPreference != null) {
            restrictedSwitchPreference.setSummary(mode);
            this.mPreference.setChecked(mode.contains("device"));
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    public void onResume() {
        if (this.mPreference == null) {
            return;
        }
        if (this.mSettingsObserver == null) {
            this.mSettingsObserver = new SettingsObserver();
        }
        this.mSettingsObserver.register(true);
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnPause
    public void onPause() {
        SettingsObserver settingsObserver;
        if (this.mPreference == null || (settingsObserver = this.mSettingsObserver) == null) {
            return;
        }
        settingsObserver.register(false);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.settingslib.development.DeveloperOptionsPreferenceController
    public void onDeveloperOptionsSwitchDisabled() {
        super.onDeveloperOptionsSwitchDisabled();
    }

    RestrictedLockUtils.EnforcedAdmin checkIfMaximumTimeToLockSetByAdmin() {
        return RestrictedLockUtilsInternal.checkIfMaximumTimeToLockIsSet(this.mContext);
    }

    /* loaded from: classes.dex */
    class SettingsObserver extends ContentObserver {
        public void register(boolean z) {
        }

        public SettingsObserver() {
            super(new Handler());
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            super.onChange(z, uri);
        }
    }

    public void setMode(final String str) {
        new Thread(new Runnable() { // from class: com.android.settings.development.UsbModeSwitchPreferenceController.1
            @Override // java.lang.Runnable
            public void run() {
                try {
                    FileWriter fileWriter = new FileWriter(UsbModeSwitchPreferenceController.this.USB_MODE_PATH, false);
                    fileWriter.write(str);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (FileNotFoundException e) {
                    Log.d("fangli", "run: " + e.toString());
                } catch (IOException e2) {
                    e2.printStackTrace();
                    Log.d("fangli", "run: " + e2.toString());
                }
            }
        }).start();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Removed duplicated region for block: B:35:0x004d A[Catch: IOException -> 0x0049, TRY_LEAVE, TryCatch #0 {IOException -> 0x0049, blocks: (B:31:0x0045, B:35:0x004d), top: B:39:0x0045 }] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x0045 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Type inference failed for: r4v0, types: [com.android.settings.development.UsbModeSwitchPreferenceController] */
    /* JADX WARN: Type inference failed for: r4v1, types: [java.io.IOException] */
    /* JADX WARN: Type inference failed for: r4v10, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v12 */
    /* JADX WARN: Type inference failed for: r4v14 */
    /* JADX WARN: Type inference failed for: r4v15, types: [java.io.BufferedReader] */
    /* JADX WARN: Type inference failed for: r4v2 */
    /* JADX WARN: Type inference failed for: r4v5 */
    /* JADX WARN: Type inference failed for: r4v6 */
    /* JADX WARN: Type inference failed for: r4v9 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public java.lang.String getMode() {
        /*
            r4 = this;
            java.lang.String r0 = ""
            r1 = 0
            java.io.FileReader r2 = new java.io.FileReader     // Catch: java.lang.Throwable -> L2c java.io.IOException -> L2f
            java.lang.String r4 = r4.USB_MODE_PATH     // Catch: java.lang.Throwable -> L2c java.io.IOException -> L2f
            r2.<init>(r4)     // Catch: java.lang.Throwable -> L2c java.io.IOException -> L2f
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch: java.lang.Throwable -> L24 java.io.IOException -> L27
            r4.<init>(r2)     // Catch: java.lang.Throwable -> L24 java.io.IOException -> L27
            java.lang.String r1 = r4.readLine()     // Catch: java.io.IOException -> L22 java.lang.Throwable -> L41
            if (r1 == 0) goto L16
            r0 = r1
        L16:
            r2.close()     // Catch: java.io.IOException -> L1d
            r4.close()     // Catch: java.io.IOException -> L1d
            goto L40
        L1d:
            r4 = move-exception
            r4.printStackTrace()
            goto L40
        L22:
            r1 = move-exception
            goto L33
        L24:
            r0 = move-exception
            r4 = r1
            goto L42
        L27:
            r4 = move-exception
            r3 = r1
            r1 = r4
            r4 = r3
            goto L33
        L2c:
            r0 = move-exception
            r4 = r1
            goto L43
        L2f:
            r4 = move-exception
            r2 = r1
            r1 = r4
            r4 = r2
        L33:
            r1.printStackTrace()     // Catch: java.lang.Throwable -> L41
            if (r2 == 0) goto L3b
            r2.close()     // Catch: java.io.IOException -> L1d
        L3b:
            if (r4 == 0) goto L40
            r4.close()     // Catch: java.io.IOException -> L1d
        L40:
            return r0
        L41:
            r0 = move-exception
        L42:
            r1 = r2
        L43:
            if (r1 == 0) goto L4b
            r1.close()     // Catch: java.io.IOException -> L49
            goto L4b
        L49:
            r4 = move-exception
            goto L51
        L4b:
            if (r4 == 0) goto L54
            r4.close()     // Catch: java.io.IOException -> L49
            goto L54
        L51:
            r4.printStackTrace()
        L54:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.development.UsbModeSwitchPreferenceController.getMode():java.lang.String");
    }
}
