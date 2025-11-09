package com.android.settings.security;

import android.content.Context;
import android.security.keystore2.AndroidKeyStoreLoadStoreParameter;
import androidx.preference.PreferenceScreen;
import com.android.settingslib.RestrictedPreference;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;
import java.security.KeyStore;
/* loaded from: classes.dex */
public class ResetCredentialsPreferenceController extends RestrictedEncryptionPreferenceController implements LifecycleObserver, OnResume {
    private final KeyStore mKeyStore;
    private RestrictedPreference mPreference;
    private final KeyStore mWifiKeyStore;

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public String getPreferenceKey() {
        return "credentials_reset";
    }

    public ResetCredentialsPreferenceController(Context context, Lifecycle lifecycle) {
        super(context, "no_config_credentials");
        KeyStore keyStore;
        KeyStore keyStore2 = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
        } catch (Exception unused) {
            keyStore = null;
        }
        this.mKeyStore = keyStore;
        if (context.getUser().isSystem()) {
            try {
                KeyStore keyStore3 = KeyStore.getInstance("AndroidKeyStore");
                keyStore3.load(new AndroidKeyStoreLoadStoreParameter(102));
                keyStore2 = keyStore3;
            } catch (Exception unused2) {
            }
        }
        this.mWifiKeyStore = keyStore2;
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @Override // com.android.settingslib.core.AbstractPreferenceController
    public void displayPreference(PreferenceScreen preferenceScreen) {
        super.displayPreference(preferenceScreen);
        this.mPreference = (RestrictedPreference) preferenceScreen.findPreference(getPreferenceKey());
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x002b, code lost:
        if (com.android.settings.wifi.UniWifiWapiCertStore.hasAnyWapiCertInstalled() != false) goto L11;
     */
    @Override // com.android.settingslib.core.lifecycle.events.OnResume
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onResume() {
        /*
            r2 = this;
            com.android.settingslib.RestrictedPreference r0 = r2.mPreference
            if (r0 == 0) goto L33
            boolean r0 = r0.isDisabledByAdmin()
            if (r0 != 0) goto L33
            r0 = 0
            java.security.KeyStore r1 = r2.mKeyStore     // Catch: java.security.KeyStoreException -> L2e
            if (r1 == 0) goto L19
            java.util.Enumeration r1 = r1.aliases()     // Catch: java.security.KeyStoreException -> L2e
            boolean r1 = r1.hasMoreElements()     // Catch: java.security.KeyStoreException -> L2e
            if (r1 != 0) goto L2d
        L19:
            java.security.KeyStore r1 = r2.mWifiKeyStore     // Catch: java.security.KeyStoreException -> L2e
            if (r1 == 0) goto L27
            java.util.Enumeration r1 = r1.aliases()     // Catch: java.security.KeyStoreException -> L2e
            boolean r1 = r1.hasMoreElements()     // Catch: java.security.KeyStoreException -> L2e
            if (r1 != 0) goto L2d
        L27:
            boolean r1 = com.android.settings.wifi.UniWifiWapiCertStore.hasAnyWapiCertInstalled()     // Catch: java.security.KeyStoreException -> L2e
            if (r1 == 0) goto L2e
        L2d:
            r0 = 1
        L2e:
            com.android.settingslib.RestrictedPreference r2 = r2.mPreference
            r2.setEnabled(r0)
        L33:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settings.security.ResetCredentialsPreferenceController.onResume():void");
    }
}
